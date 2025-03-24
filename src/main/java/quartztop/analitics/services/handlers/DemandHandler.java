package quartztop.analitics.services.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.dtos.docsPositions.DemandPositionsDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.httpClient.OkHttpClientSender;
import quartztop.analitics.models.products.BundleEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.services.crudProduct.BundleCRUDService;
import quartztop.analitics.services.crudProduct.ProductCRUDService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandHandler {

    private final OkHttpClientSender clientSender;
    private final ProductCRUDService productCRUDService;
    private final BundleCRUDService bundleCRUDService;

    public DemandDTO prepareDemand(DemandDTO demandDTO) {
        log.info("Обработка отгрузки: проверяем товары...");

        // Проверяем, есть ли товары в БД
        List<DemandPositionsDTO> positions = demandDTO.getPositions().getRows();

        for(DemandPositionsDTO demandPositionsDTO: positions) {
            Object assortment = demandPositionsDTO.getAssortment();
            if(assortment instanceof ProductDTO productDTO) {
                demandPositionsDTO.setProductDTO(productDTO);
                demandPositionsDTO.setType("product");
                if(productCRUDService.getOptionalEntity(productDTO).isEmpty()) productCRUDService.create(productDTO);
            }
            if(assortment instanceof BundleDTO bundleDTO) {

                demandPositionsDTO.setType("bundle");
                Optional<BundleEntity> optionalBundleEntity = bundleCRUDService.getOptionalEntity(bundleDTO);
                if(optionalBundleEntity.isEmpty()) {
                    BundleDTO newBundleDTO = clientSender.getBundle(bundleDTO.getId());
                    bundleCRUDService.create(newBundleDTO);
                    demandPositionsDTO.setBundle(newBundleDTO);
                    demandPositionsDTO.setAssortment(newBundleDTO);
                } else {
                    demandPositionsDTO.setBundle(bundleDTO);
                }
            }
        }
        return demandDTO;
    }
}
