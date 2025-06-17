package quartztop.analitics.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.repositories.product.BundleRepository;
import quartztop.analitics.services.crudProduct.BundleCRUDService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BundleHandler {

    private final MySkladClient clientSender;
    private final BundleRepository bundleRepository;
    private final BundleCRUDService bundleCRUDService;
    private static boolean isDoIt = false;

    public boolean checkExecution() {
        return isDoIt;
    }

    public String downloadBundlesWithOffset() {

        if (isDoIt)  {
            log.error("return ЖДИ!!! - Операция выполняется");
            return "ЖДИ!!! - Операция выполняется: Комплекты обновляются ";
        }
        isDoIt = true;
        int offset = 0;
        int limit = 100;
        int sizeProductList = 0;
        int countOperationProduct = 0;
        int countBundlesUpdate = 0;

        do {
//            log.warn("OFFSET " + offset + " SIZE PRODUCT LIST " + sizeProductList + " COUNT PRODUCT " + countOperationProduct);
            String offsetToString = String.valueOf(offset);
            List<BundleDTO> bundleDTOList = clientSender.getListBundle(offsetToString, limit);

            countBundlesUpdate = countBundlesUpdate + checkAndCreateBundle(bundleDTOList);
            sizeProductList = bundleDTOList.size();
            countOperationProduct = countOperationProduct + sizeProductList;
            offset = offset + limit;
//            log.warn("OPERATION CONTINUED - GETTED SIZE LIST " + productDTOList.size());
        } while (sizeProductList == limit);
        isDoIt = false;

        String response = "Количество проверенных комплектов: " + countOperationProduct +
                " Количество обновленных комплектов: " + countBundlesUpdate;
        log.warn(response);
        return response;
    }

    private int checkAndCreateBundle(List<BundleDTO> bundleDTOList) {
        List<UUID> changedIds = bundleRepository.findIdsWithUpdatedChanged(bundleDTOList);

        // создаём map для быстрого доступа
        Map<UUID, BundleDTO> dtoMap = bundleDTOList.stream()
                .collect(Collectors.toMap(BundleDTO::getId, Function.identity()));

        // преобразуем нужные Dto в Entity
        List<BundleDTO> productsToUpdate = changedIds.stream()
                .map(dtoMap::get)
                .toList();

        bundleCRUDService.updateOrCreateBundleList(productsToUpdate);


        return productsToUpdate.size();
    }
}
