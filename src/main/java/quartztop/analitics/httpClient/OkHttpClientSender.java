package quartztop.analitics.httpClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.dtos.wrappers.DemandWrapperDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OkHttpClientSender {

    private final ObjectMapper objectMapper;

    OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)  // тут можешь выставить даже 30 минут, если надо
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();

    @Value("${app.integration.base-url}")
    private String baseUrl;
    @Value("${app.integration.token}")
    private String token;

    private static final int MAX_RETRIES = 5;
    private static final int DEFAULT_RETRY_AFTER = 10; // Время ожидания по умолчанию в секундах

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final String expandsDemand = "expand=owner, agent, agent.owner, contract, contract.agent, " +
            "contract.agent.owner, store, organization, positions, " +
            "positions.assortment, positions.assortment.country, positions.assortment.productFolder";

    private final String expandsBundle = "?expand=country, components.assortment, components.assortment.country, components.assortment.productFolder";
    private String offset = "";



    public DemandDTO getDemand(UUID id) {


        log.info("PRINT BASE URL " + baseUrl);
        Request request = new Request.Builder()
                .url(baseUrl + "demand/" + id + "?" + expandsDemand)
                .addHeader("Authorization",token)
                .build();
        //log.info("REQUEST " + request.toString());
        return processResponse(request, new TypeReference<>(){});
    }
    public ProductDTO getProduct(UUID id) {

        Request request = new Request.Builder()
                .url(baseUrl + "product/" + id)
                .addHeader("Authorization",token)
                .build();

        return processResponse(request, new TypeReference<>(){});
    }
    public BundleDTO getBundle(UUID id) {

        Request request = new Request.Builder()
                .url(baseUrl + "bundle/" + id + expandsBundle)
                .addHeader("Authorization",token)
                .build();

        //log.info("REQUEST " + request);
        return processResponse(request, new TypeReference<>(){});
    }
    public List<DemandDTO> getListDemandsToDay(String offset, LocalDateTime startPeriod, LocalDateTime endPeriod) {

        LocalDateTime startDate = LocalDate.of(2023,1, 1).atStartOfDay();
        String formattedStartDate = startDate.format(formatter);

        String formattedStartPeriod = startPeriod.format(formatter);
        String formattedEndPeriod = endPeriod.format(formatter);

//        LocalDateTime toDay = LocalDate.now().minusDays(7).atStartOfDay();
//        String formattedStartDateWeek = toDay.format(formatter);


        String url = baseUrl + "demand" + "?filter=updated>" + formattedStartPeriod +  ";updated<" + formattedEndPeriod +
                "&offset=" + offset + "&limit=100&" + expandsDemand;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",token)
                .build();

        DemandWrapperDTO demandWrapperDTO = processResponse(request, new TypeReference<>() {});

        return demandWrapperDTO.getRows();
    }

    @SneakyThrows
    public <T> T processResponse(Request request, TypeReference<T> typeReference) {

        int retries = 0;
        //log.info("START PROCESS RESPONSE");


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
