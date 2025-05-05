package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quartztop.analitics.dtos.actions.ActionDTO;
import quartztop.analitics.dtos.actions.TelegramActionDTO;
import quartztop.analitics.integration.botApiResponses.BuilderBotResponse;
import quartztop.analitics.integration.botApiResponses.ButtonDto;
import quartztop.analitics.responses.stockResponse.StockByCategoryResponse;
import quartztop.analitics.handlers.StockByProductAndStoreHandler;
import quartztop.analitics.services.actions.ActionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/bot")
@RequiredArgsConstructor
@Slf4j
public class BotController {

    private final StockByProductAndStoreHandler stockByProductAndStoreHandler;
    private final ActionService actionService;
    private final BuilderBotResponse builderBotResponse;

    @GetMapping("/actions")
    public ResponseEntity<List<TelegramActionDTO>> actions(){
        return ResponseEntity.ok(actionService.getTelegramDTO());
    }

    @GetMapping("/actions/{id}")
    public ResponseEntity<ActionDTO> getActionById(@PathVariable long id) {
        return ResponseEntity.ok(actionService.getDtoById(id));
    }
    @GetMapping("/actions/next")
    public ResponseEntity<TelegramActionDTO> getNextAction(@RequestParam(required = false) Long currentId) {
        Optional<TelegramActionDTO> nextAction = actionService.getNextAction(currentId);

        if (nextAction.isPresent()) {
            return ResponseEntity.ok(nextAction.get());
        }

        // Если акций нет, проверяем, был ли запрос с первым вызовом
        String message = (currentId == null) ? "🎉 Акций пока нет." : "🎉 Больше акций нет.";
        return ResponseEntity.ok(TelegramActionDTO.builder().name(message).build());
    }

    @GetMapping("/stock/search")
    public ResponseEntity<List<StockByCategoryResponse>> getProductStock(@RequestParam String search) {
        log.info("Получен запрос на остатки товаров с поиском: {}", search);
        return ResponseEntity.ok(stockByProductAndStoreHandler.getResponseStockByProductAndStore(search));
    }
    @GetMapping("/button")
    public ResponseEntity<List<ButtonDto>> getListImageButton() {
        log.info("Получен запрос на список кропок с сылками на изображения");
        return ResponseEntity.ok(builderBotResponse.listButtonsResponse());
    }
}
