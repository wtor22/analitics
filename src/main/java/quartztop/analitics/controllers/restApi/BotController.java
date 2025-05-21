package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import quartztop.analitics.dtos.BotUsersDTO;
import quartztop.analitics.dtos.actions.ActionDTO;
import quartztop.analitics.integration.botApiResponses.TelegramMessageDto;
import quartztop.analitics.responses.actions.TelegramActionDTO;
import quartztop.analitics.integration.botApiResponses.BotClient;
import quartztop.analitics.integration.botApiResponses.BuilderBotResponse;
import quartztop.analitics.integration.botApiResponses.ButtonDto;
import quartztop.analitics.responses.stock.stockResponse.StockByCategoryResponse;
import quartztop.analitics.handlers.StockByProductAndStoreHandler;
import quartztop.analitics.services.actions.ActionService;
import quartztop.analitics.utils.Region;

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
    private final BotClient botClient;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/actions")
    public ResponseEntity<List<TelegramActionDTO>> actions(){
        return ResponseEntity.ok(actionService.getTelegramDTO());
    }

    @GetMapping("/actions/{id}")
    public ResponseEntity<ActionDTO> getActionById(@PathVariable long id) {
        return ResponseEntity.ok(actionService.getDtoById(id));
    }

    @GetMapping("/actions/next")
    public ResponseEntity<TelegramActionDTO> getNextAction(@RequestParam(required = false) Long currentId,
                                                           @RequestParam Region region) {
        Optional<TelegramActionDTO> nextAction = actionService.getNextAction(currentId, region);

        if (nextAction.isPresent()) {
            return ResponseEntity.ok(nextAction.get());
        }
        // Если акций нет, проверяем, был ли запрос с первым вызовом
        String message = (currentId == null) ? "🎉 Акций пока нет." : "🎉 Больше акций нет.";
        return ResponseEntity.ok(TelegramActionDTO.builder().name(message).build());
    }

    @GetMapping("/stock/search")
    public ResponseEntity<List<StockByCategoryResponse>> getProductStock(@RequestParam String search) {
        return ResponseEntity.ok(stockByProductAndStoreHandler.getResponseStockByProductAndStore(search));
    }
    @GetMapping("/button")
    public ResponseEntity<List<ButtonDto>> getListImageButton() {
        return ResponseEntity.ok(builderBotResponse.listButtonsResponse());
    }
    @PostMapping("/button")
    public ResponseEntity<String> createButton(@RequestBody ButtonDto buttonDto) {
        return ResponseEntity.ok(botClient.createButton(buttonDto));
    }
    @DeleteMapping("/button/{id}")
    public ResponseEntity<String> deleteButton(@PathVariable("id") int id) {
        return ResponseEntity.ok(botClient.deleteButton(id));
    }

    @PutMapping("/button")
    public ResponseEntity<String> updateButton(@RequestBody ButtonDto buttonDto ) {
        return ResponseEntity.ok(botClient.updateButton(buttonDto));
    }

    @PutMapping("/button/order")
    public ResponseEntity<List<ButtonDto>> setCategoriesOrderInBotIndex(@RequestBody List<Integer> ids) {

        return ResponseEntity.ok(botClient.setButtonOrder(ids));
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<BotUsersDTO>> getBotUsersByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(botClient.searchUserByPhone(phone));
    }

    @PutMapping("/users/set-admin")
    public ResponseEntity<String> setBotUserAsAdmin(@RequestParam Long userId) {
        return ResponseEntity.ok(botClient.setBotUserAsAdmin(userId));
    }

    @DeleteMapping("/users/delete-admin")
    public ResponseEntity<String> deleteBotUserAsAdmin(@RequestParam Long userId) {
        return ResponseEntity.ok(botClient.deleteBotUserAsAdmin(userId));
    }

    @GetMapping("/users/admins")
    public ResponseEntity<List<BotUsersDTO>> getListAdmins() {
        return ResponseEntity.ok(botClient.getListAdminsBot());
    }

    @PostMapping("/message")
    public ResponseEntity<Void> getBotMessage(@RequestBody TelegramMessageDto messageDto) {
        messagingTemplate.convertAndSend("/topic/messages", messageDto);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/request")
    public ResponseEntity<Void> getBotRequest(@RequestBody List<String> listRequest) {
        messagingTemplate.convertAndSend("/topic/request", listRequest);
        return ResponseEntity.ok().build();
    }

}
