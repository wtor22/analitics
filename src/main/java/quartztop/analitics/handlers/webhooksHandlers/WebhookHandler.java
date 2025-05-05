package quartztop.analitics.handlers.webhooksHandlers;

import java.util.Map;

public interface WebhookHandler {
    boolean supports(String type);
    void handle(String id, Map<String, String> params);
}
