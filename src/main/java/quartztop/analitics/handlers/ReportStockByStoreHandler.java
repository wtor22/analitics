package quartztop.analitics.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.store.StoreDto;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.dtos.reports.StockByStore;
import quartztop.analitics.dtos.reports.StockReportRow;
import quartztop.analitics.httpClient.OkHttpClientSender;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.models.organizationData.StoreEntity;
import quartztop.analitics.services.crudOrganization.StoreCRUDService;
import quartztop.analitics.services.crudProduct.ProductCRUDService;
import quartztop.analitics.services.reports.ReportStockByStoreService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportStockByStoreHandler {

    private final ReportStockByStoreService reportStockByStoreService;
    private final MySkladClient clientSender;
    private final ProductCRUDService productCRUDService;
    private final StoreCRUDService storeCRUDService;

    private static final int COUNT_POSITION_LIMIT = 1000; // количество позиций в запросе

    private static boolean isDoIt = false;

    public String downloadReportWithOffset() {

        Long startMiles = System.currentTimeMillis();

        if (isDoIt)  {
            log.error("return ЖДИ!!! - Операция выполняется");
            return "ЖДИ!!! - Операция выполняется: загружаются данные";
        }
        isDoIt = true;
        int offset = 0;
        int sizeStockList = 0;
        int countOperationStock = 0;

        checkStoreInDb(); // Проверяю склады

        reportStockByStoreService.clearOldReport(); // Удаляю старый отчет об остатках

        List<StockReportRow> listStockStoreRowsWithMissingProduct = new ArrayList<>();

        do {
            log.warn("OFFSET " + offset + " SIZE STOCK LIST " + sizeStockList + " COUNT DEMAND " + countOperationStock);
            String offsetToString = String.valueOf(offset);
            List<StockReportRow> stockByStoreRowList = clientSender.getListStockByStore(offsetToString);

            List<StockByStore> listStockByStore = flattenWithProductAndStoreId(stockByStoreRowList);

            List<UUID> listProductsUUID = listStockByStore
                    .stream()
                    .map(StockByStore::getProductId)
                    .toList();

            List<UUID> listMissingProductUUID = productCRUDService.findIdsNotInDb(listProductsUUID); // Список Id Продуктов которых нет в БД
            List<StockReportRow> listToSave = new ArrayList<>();

            for(StockReportRow stockByStoreRow :stockByStoreRowList) {

                if(listMissingProductUUID.contains(stockByStoreRow.getProductId())) {
                    listStockStoreRowsWithMissingProduct.add(stockByStoreRow);
                    continue;
                }
                listToSave.add(stockByStoreRow);
            }

            reportStockByStoreService.createListReports(listToSave
                    .stream()
                    .flatMap(row -> row.getStockByStore().stream())
                    .toList());

            sizeStockList = stockByStoreRowList.size();
            countOperationStock = countOperationStock + sizeStockList;
            offset = offset + COUNT_POSITION_LIMIT;
            log.warn("OPERATION CONTINUED - GETTED SIZE LIST " + stockByStoreRowList.size());
        } while (sizeStockList == COUNT_POSITION_LIMIT);
        // Прохожу по списку позиций товаров которых нет в бд, получаю их, сохраняю в бд, и отправляю позиции в БД.
        if (!listStockStoreRowsWithMissingProduct.isEmpty()) {
            for (StockReportRow stockReportRow :listStockStoreRowsWithMissingProduct) {
                ProductDTO productDTO = clientSender.getProduct(stockReportRow.getProductId());

                productCRUDService.create(productDTO);
            }
            reportStockByStoreService.createListReports(listStockStoreRowsWithMissingProduct
                    .stream()
                    .flatMap(row -> row.getStockByStore().stream())
                    .toList());
        }
        isDoIt = false;

        Long stopMiles = System.currentTimeMillis();
        int executionTime = (int) (stopMiles - startMiles) / 1000;
        String time = "";
        if (executionTime > 60) {
            String minuts = String.valueOf(executionTime / 60) ;
            String sec = String.valueOf(executionTime % 60) ;
            time = time.concat(minuts).concat(" минут ").concat(sec).concat(" секунд");
        } else {
            String sec = String.valueOf(executionTime) ;
            time = time.concat(sec).concat(" секунд");
        }
        return "Операция завершена успешно, Количество обработанных позиций остатков: " + countOperationStock + " за " + time;
    }

    private void checkStoreInDb() {
        List<StoreDto> storeDtoList = clientSender.getListStoreDto();
        List<StoreEntity> storeEntityList = storeCRUDService.getListEntity();
        List<UUID> storeExistsListUUID = storeEntityList.stream().map(StoreEntity::getId).toList();

        for(StoreDto storeDto :storeDtoList) {
            if(storeExistsListUUID.contains(storeDto.getId())) continue;
            storeCRUDService.create(storeDto);
        }
    }

    public List<StockByStore> flattenWithProductAndStoreId(List<StockReportRow> rows) {
        return rows.stream()
                .flatMap(row -> {
                    UUID productId = row.getProductId(); // продукт из верхнего уровня
                    return row.getStockByStore().stream()
                            .peek(store -> {
                                store.setProductId(productId);      // проставляем продукт
                                store.extractStoreIdFromMeta();    // вытягиваем ID склада из meta
                            });
                })
                .toList();
    }
}
