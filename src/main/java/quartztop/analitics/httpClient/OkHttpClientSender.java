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

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OkHttpClientSender {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${app.integration.base-url}")
    private String baseUrl;
    @Value("${app.integration.token}")
    private String token;

    public DemandDTO getDemand(UUID id) {

        String expands = "?expand=owner, agent, agent.owner, contract, contract.agent, " +
                "contract.agent.owner, store, organization, positions, " +
                "positions.assortment, positions.assortment.country";

        log.info("PRINT BASE URL " + baseUrl);
        Request request = new Request.Builder()
                .url(baseUrl + "demand/" + id + expands )
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
        String expands = "?expand=country, components.assortment, components.assortment.country";
        Request request = new Request.Builder()
                .url(baseUrl + "bundle/" + id + expands)
                .addHeader("Authorization",token)
                .build();

        //log.info("REQUEST " + request);
        return processResponse(request, new TypeReference<>(){});
    }

    @SneakyThrows
    public <T> T processResponse(Request request, TypeReference<T> typeReference) {

        //log.info("START PROCESS RESPONSE");
        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                log.info("ERROR");
                throw new RuntimeException("Unexpected response code " + response);
            }
            ResponseBody responseBody = response.body();
            if(responseBody != null) {
                String stringBody = responseBody.string();
                //log.info(stringBody);
                return objectMapper.readValue(stringBody, typeReference);
            } else {
                throw new RuntimeException("ResponseBody is empty");
            }
        }


    }
}
