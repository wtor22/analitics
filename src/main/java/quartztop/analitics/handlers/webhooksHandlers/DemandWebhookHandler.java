package quartztop.analitics.handlers.webhooksHandlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.handlers.ReportStockByStoreHandler;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.services.crudDocs.DemandCRUDService;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandWebhookHandler implements WebhookHandler{

    private final MySkladClient clientSender;
    private final DemandCRUDService demandCRUDService;
    private final ReportStockByStoreHandler reportStockByStoreHandler;
    @Override
    public boolean supports(String type) {
        log.warn("🧪 [DemandWebhookHandler] Проверка типа: '{}' → {}", type, "Demand".equalsIgnoreCase(type));
        return "Demand".equalsIgnoreCase(type);
    }

    @Override
    @Async
    public void handle(UUID id, Map<String, String> params) {
        DemandDTO demandDTO = clientSender.getDemand(id);

        if (demandDTO == null) {
            log.error("\uD83D\uDCA5 Не удалось получить отгрузку по ID: " + id);
            return;
        }

        log.warn("\uD83D\uDEE0 DEMAND IS - " + demandDTO.getName());
        if(demandDTO.getDeleted() != null) {
            demandCRUDService.deleteDemandById(id);
            log.warn("\uD83D\uDCBE DEMAND  - " + demandDTO.getName() + " deleted");
        } else {
            log.warn("☢ STOCK start UPDATE");
            reportStockByStoreHandler.downloadReportWithOffset();
        }
    }
}
