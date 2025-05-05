package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import quartztop.analitics.handlers.AgentHandler;

@RestController
@RequestMapping("api/v1/client/agent")
@RequiredArgsConstructor
@Slf4j
public class AgentClientController {

    private final AgentHandler agentHandler;

    @GetMapping
    public ResponseEntity<String> getDemandListFromToday() {
        String response = agentHandler.downloadListAgent();
        return ResponseEntity.ok(response);
    }
}
