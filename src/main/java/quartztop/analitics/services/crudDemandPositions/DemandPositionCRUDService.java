package quartztop.analitics.services.crudDemandPositions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.docsPositions.DemandPositionsDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.models.docsPositions.DemandPositionsEntity;
import quartztop.analitics.models.products.BundleEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.repositories.docsPositions.DemandPositionsRepository;
import quartztop.analitics.services.crudDocs.DemandCRUDService;
import quartztop.analitics.services.crudProduct.BundleCRUDService;
import quartztop.analitics.services.crudProduct.ProductCRUDService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandPositionCRUDService {

    private final DemandPositionsRepository demandPositionsRepository;
    private final ProductCRUDService productCRUDService;
    private final BundleCRUDService bundleCRUDService;
    private final DemandCRUDService demandCRUDService;

    public DemandPositionsEntity create(DemandPositionsDTO demandPositionsDTO) {
        DemandPositionsEntity demandPositionsEntity = mapToEntity(demandPositionsDTO);
        if (demandPositionsDTO.getProductDTO() != null) setProduct(demandPositionsEntity, demandPositionsDTO.getProductDTO());
        if (demandPositionsDTO.getBundle() != null) setBundle(demandPositionsEntity, demandPositionsDTO.getBundle());

        return demandPositionsRepository.save(demandPositionsEntity);
    }

    private void setProduct(DemandPositionsEntity demandPositionsEntity, ProductDTO productDTO){

        Optional<ProductsEntity> optionalProductsEntity = productCRUDService.getOptionalEntity(productDTO);
        if (optionalProductsEntity.isEmpty()) {
            log.info("Product {} NOT FOUND", productDTO.getArticle());
            ProductsEntity productsEntity = productCRUDService.create(productDTO);
            demandPositionsEntity.setProducts(productsEntity);
            log.info("Product {} was created", productsEntity.getArticle());
        } else {
            demandPositionsEntity.setProducts(optionalProductsEntity.get());
        }
    }

    private void setBundle(DemandPositionsEntity demandPositionsEntity, BundleDTO bundleDTO){
        Optional<BundleEntity> optionalBundleEntity = bundleCRUDService.getOptionalEntity(bundleDTO);
        if(optionalBundleEntity.isEmpty()) {
            log.info("Bundle {} NOT FOUND", bundleDTO.getArticle());
            BundleEntity bundleEntity = bundleCRUDService.create(bundleDTO);
            demandPositionsEntity.setBundle(bundleEntity);
            log.info("Bundle {} was created", bundleEntity.getArticle());
        }

    }


    public static DemandPositionsEntity mapToEntity(DemandPositionsDTO demandPosition) {

        DemandPositionsEntity demandPositionsEntity = new DemandPositionsEntity();
        demandPositionsEntity.setId(demandPosition.getId());
        demandPositionsEntity.setSum(demandPosition.getSum());
        demandPositionsEntity.setDiscount(demandPosition.getDiscount());
        demandPositionsEntity.setType(demandPosition.getType());
        demandPositionsEntity.setQuantity(demandPosition.getQuantity());
        return demandPositionsEntity;
    }
    public static DemandPositionsDTO mapToDTO(DemandPositionsEntity demandPosition) {

        DemandPositionsDTO demandPositionsDTO = new DemandPositionsDTO();
        demandPositionsDTO.setId(demandPosition.getId());
        demandPositionsDTO.setSum(demandPosition.getSum());
        demandPositionsDTO.setDiscount(demandPosition.getDiscount());
        demandPositionsDTO.setType(demandPosition.getType());
        demandPositionsDTO.setQuantity(demandPosition.getQuantity());
        return demandPositionsDTO;
    }
}
