package quartztop.analitics.integration.botApiResponses;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import quartztop.analitics.integration.OkkHttpClient;

import java.util.List;

@Component
@Slf4j
public class BotClient extends OkkHttpClient {

    BotClient(ObjectMapper objectMapper) {
        super(objectMapper);
    }


    StatisticsResponses getStatisticsResponse() {

        Request request = new Request.Builder()
                .url(super.botBaseUrl + "/statistics")
                .build();
        try {
            return processResponse(request, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота: ");
            return null;
        }
    }

    List<ButtonDto> getAllButton() {

        Request request = new Request.Builder()
                .url(super.botBaseUrl + "/button")
                .build();
        try {
            return processResponse(request, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота: ");
            return null;
        }

    }
}
