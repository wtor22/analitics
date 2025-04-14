package quartztop.analitics.services.reports;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.reports.StockByStore;
import quartztop.analitics.models.organizationData.StoreEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.models.reports.StockByStoreEntity;
import quartztop.analitics.repositories.reports.ReportsStockByStoreRepository;
import quartztop.analitics.services.crudOrganization.StoreCRUDService;
import quartztop.analitics.services.crudProduct.ProductCRUDService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportStockByStoreService {

    private final ReportsStockByStoreRepository reportsStockByStoreRepository;
    private final StoreCRUDService storeCRUDService;
    private final ProductCRUDService productCRUDService;

    public StockByStoreEntity createReport(StockByStore stockByStore) {
        return reportsStockByStoreRepository.save(getEntity(stockByStore));
    }

    public List<StockByStoreEntity> getStockBySearch(String search) {
        return reportsStockByStoreRepository.getStockReportsBySearch(search);
    }

    public void createListReports(List<StockByStore> reportStockByStoreDTOList) {

        List<StockByStoreEntity> stockByStoreEntityList = new ArrayList<>();

        for (StockByStore stockByStore : reportStockByStoreDTOList) {
            UUID productId = stockByStore.getProductId();
            UUID storeId = stockByStore.getStoreId();

            ProductsEntity productsEntity = productCRUDService.getOptionalEntityById(productId)
                    .orElse(null);
            if (productsEntity == null) {
                log.error("PRODUCT WITH ID " + productId + " NOT FOUND IN DB !!!");
                continue;
            }
            StoreEntity storeEntity = storeCRUDService.getOptionalEntityById(storeId).orElse(null);

            if (storeEntity == null) {
                log.error("STORE WITH ID " + storeId + " NOT FOUND IN DB !!!");
                continue;
            }
            StockByStoreEntity stockByStoreEntity = mapToEntity(stockByStore);
            stockByStoreEntity.setProductsEntity(productsEntity);
            stockByStoreEntity.setStoreEntity(storeEntity);

            stockByStoreEntityList.add(stockByStoreEntity);
        }

        log.error("INCOME LIST DTO TO SAVE SIZE " + reportStockByStoreDTOList.size());
        log.error("LIST ENTITY TO SAVE SIZE" + stockByStoreEntityList.size());

        reportsStockByStoreRepository.saveAll(stockByStoreEntityList);
    }

    private StockByStoreEntity getEntity(StockByStore stockByStore) {
        StockByStoreEntity reportStockByStoreEntity = mapToEntity(stockByStore);

//        Optional<StoreEntity> optionalStoreEntity = storeCRUDService
//                .getOptionalEntityById(stockByStore.getStoreId());
//        optionalStoreEntity.ifPresent(reportStockByStoreEntity::setStoreEntity);
        return reportStockByStoreEntity;
    }

    public StockByStoreEntity mapToEntity(StockByStore stockByStore) {

        StockByStoreEntity reportStockByStoreEntity = new StockByStoreEntity();

        reportStockByStoreEntity.setStock(stockByStore.getStock());
        reportStockByStoreEntity.setStoreName(stockByStore.getName());
        reportStockByStoreEntity.setReserve(stockByStore.getReserve());
        reportStockByStoreEntity.setInTransit(stockByStore.getInTransit());

        return reportStockByStoreEntity;
    }

    public void clearOldReport() {
        reportsStockByStoreRepository.deleteAll();
    }
}
