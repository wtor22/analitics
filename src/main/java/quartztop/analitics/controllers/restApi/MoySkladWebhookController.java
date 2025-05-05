package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.services.counterparty.AgentCRUDService;

@RestController
@RequestMapping("api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class MoySkladWebhookController {

    private final MySkladClient clientSender;
    private final AgentCRUDService agentCRUDService;

    @PostMapping("/agent")
    public ResponseEntity<Void> receiveAgentWebhook(@RequestParam("id") String id) {

        log.warn("ПОЛУЧЕН WEBHOOK id: " + id );

        AgentDTO agentDTO = clientSender.getAgent(id);

        log.warn("AGENT IS - " + agentDTO.getName());

        log.warn("AGENT SAVED " + agentCRUDService.create(agentDTO).getUpdated());
        return ResponseEntity.ok().build();
    }
}
