package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quartztop.analitics.handlers.webhooksHandlers.WebhookHandler;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class MoySkladWebhookController {


    private final List<WebhookHandler> handlers;


    @PostMapping
    public ResponseEntity<Void> receiveAgentWebhook(
            @RequestParam String id,
            @RequestParam String type,
            @RequestParam Map<String, String> params // вдруг потом другие параметры появятся
    ) {
        long start = System.currentTimeMillis();
        log.warn("ПОЛУЧЕН WEBHOOK type: {} id: {}", type, id);

        handlers.stream()
                .filter(h -> h.supports(type))
                .findFirst()
                .ifPresentOrElse(
                        h -> h.handle(id, params),
                        () -> log.warn("Нет обработчика для типа {}", type)
                );
        log.error("Затраченное время " + (System.currentTimeMillis() - start));

        return ResponseEntity.ok().build();
    }
}
