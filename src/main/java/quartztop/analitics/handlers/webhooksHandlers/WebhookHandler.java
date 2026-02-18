package quartztop.analitics.handlers.webhooksHandlers;

import java.util.Map;
import java.util.UUID;

public interface WebhookHandler {
    boolean supports(String type);
    void handle(UUID id, Map<String, String> params);
}
