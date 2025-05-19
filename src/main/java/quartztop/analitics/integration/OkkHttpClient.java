package quartztop.analitics.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OkkHttpClient {

    protected final ObjectMapper objectMapper;

    OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();

    @Value("${app.integration.base-url}")
    protected String baseUrl;
    @Value("${app.integration.token}")
    protected String token;
    @Value("${app.bot-integration.base-url}")
    protected String botBaseUrl;

    private static final int MAX_RETRIES = 5;
    private static final int DEFAULT_RETRY_AFTER = 10; // Время ожидания по умолчанию в секундах

    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    @SneakyThrows
    public String simpleTextResponse(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {

            String responseText;
            ResponseBody body = response.body();
            if (body != null) {
                responseText = body.string();
            } else {
                responseText = "Пустой ответ";
            }
            return responseText;
        } catch (Exception e) {
            log.error("❌ Ошибка в simpleTextResponse:", e);
            return "❌ Ошибка при обращении к серверу бота";
        }
    }
    @SneakyThrows
    public <T> T processResponse(Request request, TypeReference<T> typeReference) {

        int retries = 0;

        while (retries < MAX_RETRIES) {
            try (Response response = httpClient.newCall(request).execute()) {

                if (response.code() == 429) {
                    int retryAfter = getRetryAfter(response);
                    log.error("Too many requests (429). Waiting " + retryAfter + " seconds before retry # " + retries + 1);
                    waitForRetry(retryAfter);
                    retries++;
                    continue;
                } else if (!response.isSuccessful()) {
                    log.warn("Unexpected response code: " + response.code() + " — " + response.message());
                    throw new RuntimeException("Unexpected response code " + response);
                }
                ResponseBody responseBody = response.body();

                if(responseBody != null) {
                    try {
                        String stringBody = responseBody.string();
                        //log.info(stringBody);
                        return objectMapper.readValue(stringBody, typeReference);
                    } finally {
                        responseBody.close();
                    }
                } else {
                    throw new RuntimeException("ResponseBody is empty");
                }
            } catch (IOException e) {
                log.info("Request failed: " + e.getMessage());
                throw e; // Пробрасываем исключение дальше
            }
        }
        throw new RuntimeException("Max retries reached. Request failed.");
    }

    private void waitForRetry(int retryAfter) throws IOException {
        try {
            TimeUnit.SECONDS.sleep(retryAfter);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Thread interrupted during sleep", e);
        }
    }

    private int getRetryAfter(Response response) {
        String retryAfterHeader = response.header("Retry-After");
        if (retryAfterHeader != null) {
            try {
                int timeToWait = Integer.parseInt(retryAfterHeader);
                log.error("Wait " + timeToWait + "sec");
                return timeToWait;
            } catch (NumberFormatException e) {
                log.error("Invalid Retry-After header value: " + retryAfterHeader);
            }
        }
        return DEFAULT_RETRY_AFTER;
    }
}
