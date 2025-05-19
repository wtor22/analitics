package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quartztop.analitics.integration.botApiResponses.BotClient;
import quartztop.analitics.integration.botApiResponses.StatisticsByDateDTO;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/bot/statistics")
@Slf4j
@RequiredArgsConstructor
public class BotStatistics {

    private final BotClient botClient;

    @GetMapping("/by-period")
    public StatisticsByDateDTO getStatisticsByPeriod(LocalDate start, LocalDate end) {

        StatisticsByDateDTO response = botClient.getStatisticsByPeriod(start, end);
        log.error("START CONTROLLER STATISTICS dates " + response.getCountClickAction());
        return response;
    }
}
