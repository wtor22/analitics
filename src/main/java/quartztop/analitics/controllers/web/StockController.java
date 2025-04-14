package quartztop.analitics.controllers.web;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quartztop.analitics.handlers.ReportStockByStoreHandler;

@RestController
@RequestMapping("api/v1/client/report/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final ReportStockByStoreHandler reportStockByStoreHandler;

    @GetMapping("/by-store")
    public ResponseEntity<String> getStocks() {

        log.error("START GET STOCK CONTROLLER");
        return ResponseEntity.ok(reportStockByStoreHandler.downloadReportWithOffset());

    }
}
