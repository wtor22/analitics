package quartztop.analitics.integration.botApiResponses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.actions.ActionDTO;
import quartztop.analitics.services.actions.ActionService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuilderBotResponse {

    private final BotClient botClient;
    private final ActionService actionService;



    public StatisticsResponses statisticsResponses() {

        StatisticsResponses statisticsResponses = botClient.getStatisticsResponse();
        if(statisticsResponses == null) {
            return null;
        }

        statisticsResponses.getActionStatisticsList().forEach(r -> {
            ActionDTO actionDTO = actionService.getDtoById(r.getActionId());
            r.setActionDTO(actionDTO);
        });
        return statisticsResponses;
    }

    public List<ButtonDto> listButtonsResponse() {
        return  botClient.getAllButton();
    }

}
