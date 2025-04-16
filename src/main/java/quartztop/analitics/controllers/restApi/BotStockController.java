package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quartztop.analitics.responses.stockResponse.StockByCategoryResponse;
import quartztop.analitics.handlers.StockByProductAndStoreHandler;

import java.util.List;

@RestController
@RequestMapping("api/v1/bot/stock")
@RequiredArgsConstructor
@Slf4j
public class BotStockController {

    private final StockByProductAndStoreHandler stockByProductAndStoreHandler;


    @GetMapping("/search")
    public ResponseEntity<List<StockByCategoryResponse>> getProductStock(@RequestParam String search) {
        log.info("Получен запрос на остатки товаров с поиском: {}", search);
        return ResponseEntity.ok(stockByProductAndStoreHandler.getResponseStockByProductAndStore(search));
    }
}
