package quartztop.analitics.handlers.webhooksHandlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.services.counterparty.AgentCRUDService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentWebhookHandler implements WebhookHandler{

    private final MySkladClient clientSender;
    private final AgentCRUDService agentCRUDService;

    @Override
    public boolean supports(String type) {
        return "Company".equalsIgnoreCase(type);
    }

    @Override
    @Async
    public void handle(String id, Map<String, String> params) {
        AgentDTO agentDTO = clientSender.getAgent(id);

        log.warn("AGENT IS - " + agentDTO.getName());

        log.warn("AGENT SAVED " + agentCRUDService.create(agentDTO).getUpdated());
    }
}
