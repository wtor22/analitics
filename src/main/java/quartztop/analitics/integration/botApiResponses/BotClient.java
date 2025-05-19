package quartztop.analitics.integration.botApiResponses;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;
import quartztop.analitics.dtos.BotUsersDTO;
import quartztop.analitics.integration.OkkHttpClient;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.nio.charset.StandardCharsets;

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

    public StatisticsByDateDTO getStatisticsByPeriod(LocalDate start, LocalDate end) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startStr = URLEncoder.encode(start.format(formatter), StandardCharsets.UTF_8);
        String endStr = URLEncoder.encode(end.format(formatter), StandardCharsets.UTF_8);

        log.error("START REQUEST ");
        Request request = new Request.Builder()
                .url(super.botBaseUrl + "/statistics-by-period?start=" + startStr + "&end=" + endStr)
                .build();

        try {
            return processResponse(request, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота при получении статистики за период: ");
            return null;
        }

    }

    public List<BotUsersDTO> getListAdminsBot() {
        Request request = new Request.Builder()
                .url(super.botBaseUrl + "/users/admins")
                .build();
        try {
            return processResponse(request, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота при запросе списка администраторов ");
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

    public String deleteButton(int id) {
        Request request = new Request.Builder()
                .url(super.botBaseUrl + "/button/" + id)
                .delete()
                .build();

        return simpleTextResponse(request);
    }

    public List<ButtonDto> setButtonOrder(List<Integer> ids) {

        try {
            String json = objectMapper.writeValueAsString(ids);

            Request request = new Request.Builder()
                    .url(super.botBaseUrl + "/button/order")
                    .put(RequestBody.create(json, MediaType.get("application/json")))
                    .build();
            return processResponse(request, new TypeReference<>() {});

        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота");
            return null;
        }
    }

    public String updateButton(ButtonDto buttonDto) {
        try {
            String json = objectMapper.writeValueAsString(buttonDto);
            Request request = new Request.Builder()
                    .url(super.botBaseUrl + "/button")
                    .put(RequestBody.create(json, MediaType.get("application/json")))
                    .build();
            return simpleTextResponse(request);
        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота: ");
            return "❌ Ошибка при обращении к серверу бота: ";
        }
    }

    public String createButton(ButtonDto buttonDto) {
        try {
            String json = objectMapper.writeValueAsString(buttonDto);
            Request request = new Request.Builder()
                    .url(super.botBaseUrl + "/button")
                    .post(RequestBody.create(json, MediaType.get("application/json")))
                    .build();

            return simpleTextResponse(request);

        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота: ");
            return "❌ Ошибка при обращении к серверу бота: ";
        }
    }

    public List<BotUsersDTO> searchUserByPhone(String partPhone) {

        try {
            Request request = new Request.Builder()
                    .url(super.botBaseUrl + "/users/search?phone=" + partPhone)
                    .build();
            return processResponse(request, new TypeReference<>() {
            });

        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота: ");
            return null;
        }
    }

    public String setBotUserAsAdmin(Long telegramId) {

        try {
            String json = objectMapper.writeValueAsString(telegramId);
            Request request = new Request.Builder()
                    .url(super.botBaseUrl + "/users/set-admin")
                    .put(RequestBody.create(json, MediaType.get("application/json")))
                    .build();

            String responseText = simpleTextResponse(request);
            log.error("PRINT RESPONSE " + responseText);
            return responseText;

        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота при сохранении админа  ");
            return "❌ Ошибка при обращении к серверу бота";
        }
    }

    public String deleteBotUserAsAdmin(Long telegramId) {

        try {
            String json = objectMapper.writeValueAsString(telegramId);
            Request request = new Request.Builder()
                    .url(super.botBaseUrl + "/users/unset-admin")
                    .put(RequestBody.create(json, MediaType.get("application/json")))
                    .build();

            String responseText = simpleTextResponse(request);
            log.error("PRINT RESPONSE " + responseText);
            return responseText;

        } catch (Exception e) {
            log.error("❌ Ошибка при обращении к серверу бота при сохранении админа  ");
            return "❌ Ошибка при обращении к серверу бота";
        }
    }


}
