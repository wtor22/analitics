package quartztop.analitics.services.shedules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import quartztop.analitics.handlers.BundleHandler;
import quartztop.analitics.handlers.DemandHandler;
import quartztop.analitics.handlers.ProductHandler;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final DemandHandler demandHandler;
    private final ProductHandler productHandler;
    private final BundleHandler bundleHandler;


    // Крон: каждый день в 1:00 импорт отгрузок за предыдущие сутки
    @Scheduled(cron = "0 0 1 * * *")
    public void reportCurrentTime() {

        LocalDate lastDay = LocalDate.now().minusDays(1);
        log.warn("START SCHEDULED FOR " + lastDay);
        demandHandler.downloadDemandsWithOffset(lastDay, lastDay);
    }

    // Крон: каждый день в 2:00 обновление товаров и комплектов
    @Scheduled(cron = "0 0 2 * * *")
    public void updateProductsAndBundles() {

        long start = System.currentTimeMillis(); // ⏱ старт

        productHandler.downloadProductsWithOffset();
        bundleHandler.downloadBundlesWithOffset();

        long end = System.currentTimeMillis(); // ⏱ финиш
        long durationMillis = end - start;

        log.warn("PRODUCTS AND BUNDLES UPDATED in {} seconds ({} ms)",
                durationMillis / 1000.0, durationMillis);
    }
}
