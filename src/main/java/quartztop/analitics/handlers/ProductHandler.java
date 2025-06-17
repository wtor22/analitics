package quartztop.analitics.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.repositories.product.ProductRepository;
import quartztop.analitics.services.crudProduct.ProductCRUDService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductHandler {

    private final MySkladClient clientSender;
    private final ProductCRUDService productCRUDService;
    private final ProductRepository productRepository;

    private static boolean isDoIt = false;

    public boolean checkExecution() {
        return isDoIt;
    }


    public String downloadProductsWithOffset() {

        if (isDoIt)  {
            log.error("return ЖДИ!!! - Операция выполняется");
            return "ЖДИ!!! - Операция выполняется: Товары обновляются ";
        }
        isDoIt = true;
        int offset = 0;
        int limit = 100;
        int sizeProductList = 0;
        int countOperationProduct = 0;
        int countProductsUpdate = 0;

        do {
//            log.warn("OFFSET " + offset + " SIZE PRODUCT LIST " + sizeProductList + " COUNT PRODUCT " + countOperationProduct);
            String offsetToString = String.valueOf(offset);
            List<ProductDTO> productDTOList = clientSender.getListProducts(offsetToString, limit);

            countProductsUpdate = countProductsUpdate + checkAndCreateProduct(productDTOList);
            sizeProductList = productDTOList.size();
            countOperationProduct = countOperationProduct + sizeProductList;

            offset = offset + limit;
//            log.warn("OPERATION CONTINUED - GETTED SIZE LIST " + productDTOList.size());
        } while (sizeProductList == limit);
        isDoIt = false;
        String response = "Количество проверенных товаров: " + countOperationProduct +
                " Количество обновленных товаров: " + countProductsUpdate;
        log.warn(response);
        return response;
    }


    private int checkAndCreateProduct(List<ProductDTO> productDTOList) {
        List<UUID> changedIds = productRepository.findIdsWithUpdatedChanged(productDTOList);

        // создаём map для быстрого доступа
        Map<UUID, ProductDTO> dtoMap = productDTOList.stream()
                .collect(Collectors.toMap(ProductDTO::getId, Function.identity()));

        // преобразуем нужные Dto в Entity
        List<ProductDTO> productsToUpdate = changedIds.stream()
                .map(dtoMap::get)
                .toList();

        return productCRUDService.updateOrCreateListProducts(productsToUpdate);
    }
}
