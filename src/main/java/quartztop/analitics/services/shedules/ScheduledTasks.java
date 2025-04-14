package quartztop.analitics.services.shedules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import quartztop.analitics.handlers.DemandHandler;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final DemandHandler demandHandler;


    // Крон: каждый день в 1:00 импорт отгрузок за предыдущие сутки
    @Scheduled(cron = "0 0 1 * * *")
    public void reportCurrentTime() {

        LocalDate lastDay = LocalDate.now().minusDays(1);
        log.warn("START SCHEDULED FOR " + lastDay);
        demandHandler.downloadDemandsWithOffset(lastDay, lastDay);
    }
}
