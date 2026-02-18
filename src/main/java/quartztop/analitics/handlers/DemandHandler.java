package quartztop.analitics.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.dtos.docsPositions.DemandPositionsDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.httpClient.OkHttpClientSender;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.models.products.BundleEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.services.crudDocs.DemandCRUDService;
import quartztop.analitics.services.crudProduct.BundleCRUDService;
import quartztop.analitics.services.crudProduct.ProductCRUDService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandHandler {

    private final MySkladClient clientSender;
    private final ProductCRUDService productCRUDService;
    private final BundleCRUDService bundleCRUDService;
    private final DemandCRUDService demandCRUDService;

    private static boolean isDoIt = false;

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

    public String downloadDemandsWithOffset(LocalDate start, LocalDate end) {

        if (isDoIt)  {
            log.error("⏳ ЖДИ!!! - Операция выполняется");
        return "⏳ ЖДИ!!! - Операция выполняется: загружаются данные за период с " + start + " по " + end;
        }
        isDoIt = true;
        int offset = 0;
        int sizeDemandList = 0;
        int countOperationDemand = 0;

        LocalDateTime startPeriod = start.atStartOfDay();
        LocalDateTime endPeriod = end.atTime(23,59,59);
        do {
            log.warn("м OFFSET " + offset + " SIZE DEMANDLIST " + sizeDemandList + " COUNT DEMAND " + countOperationDemand);
            String offsetToString = String.valueOf(offset);
            List<DemandDTO> demandDTOList = clientSender.getListDemandsToDay(offsetToString, startPeriod, endPeriod);
            checkAndCreateDemands(demandDTOList);
            sizeDemandList = demandDTOList.size();
            countOperationDemand = countOperationDemand + sizeDemandList;
            offset = offset + 100;
            log.warn("\uD83D\uDEE0 OPERATION CONTINUED - GETTED SIZE LIST " + demandDTOList.size());
        } while (sizeDemandList == 100);
        isDoIt = false;
        log.warn("\uD83D\uDEE0 Операция завершена успешно, Количество обработанных отгрузок: " + countOperationDemand);
        return "Операция завершена успешно, Количество обработанных отгрузок: " + countOperationDemand;
    }

    private void checkAndCreateDemands(List<DemandDTO> demandDTOList) {
        List<DemandDTO> listDemandsToSave = new ArrayList<>();
        for (DemandDTO demandDTO: demandDTOList) {
            //log.info("CHECK DEMAND ID " + demandDTO.getId());
            if (demandDTO.getDeleted() != null) {
                demandCRUDService.deleteDemandById(demandDTO.getId());
                continue;
            }
            LocalDateTime momentUpdateDemandFromStorage = demandCRUDService.getMomentUpdateById(demandDTO.getId());
            if (momentUpdateDemandFromStorage == demandDTO.getUpdated()) continue;
            listDemandsToSave.add(demandDTO);
            prepareDemand(demandDTO);
        }
        listDemandsToSave.forEach(demandCRUDService::create);
    }
}
