package quartztop.analitics.integration.mySkladIntegration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.dtos.counterparty.AgentWrapper;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.dtos.docs.DemandWrapperDTO;
import quartztop.analitics.dtos.organizationData.store.StoreDto;
import quartztop.analitics.dtos.organizationData.store.StoreWrapper;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.BundleWrapperDto;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.dtos.products.ProductWrapperDto;
import quartztop.analitics.dtos.reports.ReportStockByStoreWrapper;
import quartztop.analitics.dtos.reports.StockReportRow;
import quartztop.analitics.integration.OkkHttpClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class MySkladClient extends OkkHttpClient {


    private final String expandsDemand = "expand=owner, agent, agent.owner, contract, contract.agent, " +
            "contract.agent.owner, store, organization, positions, " +
            "positions.assortment, positions.assortment.country, positions.assortment.productFolder";

    private final String expandsBundle = "?expand=country, components.assortment, components.assortment.country, components.assortment.productFolder";

    private final String expandsProduct = "?expand=country, productFolder";
    private final String offset = "";

    public MySkladClient(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    public DemandDTO getDemand(UUID id) {
        Request request = new Request.Builder()
                .url(baseUrl + "entity/demand/" + id + "?" + expandsDemand)
                .addHeader("Authorization",token)
                .build();
        //log.info("REQUEST " + request.toString());
        return processResponse(request, new TypeReference<>(){});
    }

    public ProductDTO getProduct(UUID id) {

        Request request = new Request.Builder()
                .url(baseUrl + "entity/product/" + id + expandsProduct)
                .addHeader("Authorization",token)
                .build();
        return processResponse(request, new TypeReference<>(){});
    }

    public List<ProductDTO> getListProducts(String offset, int limit) {
        Request request = new Request.Builder()
                .url(baseUrl + "entity/product/" + expandsProduct + "&limit=" + limit + "&offset=" + offset)
                .addHeader("Authorization",token)
                .build();
        ProductWrapperDto productWrapperDto = processResponse(request, new TypeReference<>() {});
        return productWrapperDto.getRows();
    }

    public List<BundleDTO> getListBundle(String offset, int limit) {
        Request request = new Request.Builder()
                .url(baseUrl + "entity/bundle/" + expandsBundle + "&limit=" + limit + "&offset=" + offset)
                .addHeader("Authorization",token)
                .build();
        BundleWrapperDto bundleWrapperDto = processResponse(request, new TypeReference<>() {});
        return bundleWrapperDto.getRows();
    }
    public AgentDTO getAgent(UUID id) {

        Request request = new Request.Builder()
                .url(baseUrl + "entity/counterparty/" + id + "?expand=owner")
                .addHeader("Authorization",token)
                .build();
        return processResponse(request, new TypeReference<>() {});
    }

    public BundleDTO getBundle(UUID id) {
        Request request = new Request.Builder()
                .url(baseUrl + "entity/bundle/" + id + expandsBundle)
                .addHeader("Authorization",token)
                .build();
        return processResponse(request, new TypeReference<>(){});
    }

    public List<DemandDTO> getListDemandsToDay(String offset, LocalDateTime startPeriod, LocalDateTime endPeriod) {

        String formattedStartPeriod = startPeriod.format(formatter);
        String formattedEndPeriod = endPeriod.format(formatter);

        String url = baseUrl + "entity/demand" + "?filter=updated>" + formattedStartPeriod +  ";updated<" + formattedEndPeriod +
                "&offset=" + offset + "&limit=100&" + expandsDemand;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",token)
                .build();

        DemandWrapperDTO demandWrapperDTO = processResponse(request, new TypeReference<>() {});

        return demandWrapperDTO.getRows();
    }
    public List<StockReportRow> getListStockByStore(String offset) {
        String url = baseUrl + "report/stock/bystore?offset=" + offset;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();

        ReportStockByStoreWrapper reportStockByStoreWrapper = processResponse(request, new TypeReference<ReportStockByStoreWrapper>() {});

        return reportStockByStoreWrapper.getRows();
    }
    public List<AgentDTO> getListAgent(String offset) {
        String url = baseUrl + "entity/counterparty" + "?offset=" + offset  + "&limit=100&expand=owner";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();
        AgentWrapper agentWrapper =  processResponse(request, new TypeReference<>() {});
        return agentWrapper.getRows();
    }

    public List<StoreDto> getListStoreDto() {
        Request request = new Request.Builder()
                .url(baseUrl + "entity/store")
                .addHeader("Authorization",token)
                .build();
        StoreWrapper storeWrapper = processResponse(request, new TypeReference<>() {});
        return storeWrapper.getRows();
    }
}
