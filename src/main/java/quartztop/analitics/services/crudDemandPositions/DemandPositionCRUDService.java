package quartztop.analitics.services.crudDemandPositions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.docsPositions.DemandPositionsDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.GroupAgentEntity;
import quartztop.analitics.models.docsPositions.DemandPositionsEntity;
import quartztop.analitics.models.products.BundleEntity;
import quartztop.analitics.models.products.CategoryEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.repositories.docsPositions.DemandPositionsRepository;
import quartztop.analitics.services.crudProduct.BundleCRUDService;
import quartztop.analitics.services.crudProduct.ProductCRUDService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandPositionCRUDService {

    private final DemandPositionsRepository demandPositionsRepository;
    private final ProductCRUDService productCRUDService;
    private final BundleCRUDService bundleCRUDService;

    public DemandPositionsEntity create(DemandPositionsDTO demandPositionsDTO) {
        DemandPositionsEntity demandPositionsEntity = mapToEntity(demandPositionsDTO);

        Object assortment = demandPositionsDTO.getAssortment();
        if(assortment instanceof ProductDTO) demandPositionsDTO.setProductDTO((ProductDTO) assortment);
        if(assortment instanceof BundleDTO) demandPositionsDTO.setBundle((BundleDTO) assortment);

        if (demandPositionsDTO.getProductDTO() != null) setProduct(demandPositionsEntity, demandPositionsDTO.getProductDTO());
        if (demandPositionsDTO.getBundle() != null) setBundle(demandPositionsEntity, demandPositionsDTO.getBundle());

        return demandPositionsRepository.save(demandPositionsEntity);
    }

    public List<DemandPositionsEntity> getListEntity(LocalDateTime periodStart, LocalDateTime periodEnd, GroupAgentEntity groupAgentEntity) {
        return demandPositionsRepository.findAllByPeriodAndTags(periodStart, periodEnd, groupAgentEntity);
    }
    public List<CategoryEntity> getListUniqueCategoryByPeriodAndGroupAgent(LocalDateTime periodStart, LocalDateTime periodEnd, GroupAgentEntity groupAgentEntity) {
        return demandPositionsRepository.getListUniqueCategoryEntity(periodStart, periodEnd, groupAgentEntity);
    }

    public List<CategoryEntity> getListUniqueCategoryByPeriodAndAgent(LocalDateTime periodStart, LocalDateTime periodEnd, AgentEntity agent, List<UUID> listUUIDCategory) {
        return demandPositionsRepository.getListUniqueCategoryEntityByAgent(periodStart, periodEnd, agent, listUUIDCategory);
    }

    public double getCountProductByPeriodAndAgentAndCategory(LocalDateTime periodStart, LocalDateTime periodEnd, CategoryEntity categoryEntity, AgentEntity agent) {
        return demandPositionsRepository.getQuantityProductByAgentAndCategory(periodStart,periodEnd,categoryEntity,agent);
    }

    public double getSumPriceByPeriodAndAgentAndCategory(LocalDateTime periodStart, LocalDateTime periodEnd, CategoryEntity categoryEntity, AgentEntity agent) {
        return demandPositionsRepository.getSumPriceByAgentAndCategory(periodStart,periodEnd,categoryEntity,agent);
    }

//    public List<DemandPositionsEntity> getListEntityByPeriodAndAgentAndCategory(LocalDateTime periodStart, LocalDateTime periodEnd, CategoryEntity categoryEntity, AgentEntity agent) {
//        return demandPositionsRepository.getListPositionEntityByAgentAndCategory(periodStart, periodEnd,categoryEntity,agent);
//    }

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
        //log.info("START MAPTOENTITY FOR " + demandPosition);
        DemandPositionsEntity demandPositionsEntity = new DemandPositionsEntity();
        demandPositionsEntity.setId(demandPosition.getId());
        demandPositionsEntity.setPrice(demandPosition.getPrice());
        demandPositionsEntity.setDiscount(demandPosition.getDiscount());
        demandPositionsEntity.setType(demandPosition.getType());
        demandPositionsEntity.setQuantity(demandPosition.getQuantity());

        return demandPositionsEntity;
    }
    public static DemandPositionsDTO mapToDTO(DemandPositionsEntity demandPosition) {

        DemandPositionsDTO demandPositionsDTO = new DemandPositionsDTO();
        demandPositionsDTO.setId(demandPosition.getId());
        demandPositionsDTO.setPrice(demandPosition.getPrice());
        demandPositionsDTO.setDiscount(demandPosition.getDiscount());
        demandPositionsDTO.setType(demandPosition.getType());
        demandPositionsDTO.setQuantity(demandPosition.getQuantity());
        return demandPositionsDTO;
    }
}
