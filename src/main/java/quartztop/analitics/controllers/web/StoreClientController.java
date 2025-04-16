package quartztop.analitics.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quartztop.analitics.dtos.organizationData.store.StoreAliasDTO;
import quartztop.analitics.dtos.organizationData.store.StoreDto;
import quartztop.analitics.responses.storeResponse.StoreResponse;
import quartztop.analitics.services.crudOrganization.StoreCRUDService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/client/store")
@RequiredArgsConstructor
@Slf4j
public class StoreClientController {

    private final StoreCRUDService storeCRUDService;

    @GetMapping("/getStores")
    public ResponseEntity<StoreResponse> getAvailableStoresAvailableList() {
        return ResponseEntity.ok(getStoresResponse());
    }

    @GetMapping("/getStoreNotAvailable")
    public ResponseEntity<List<StoreDto>> getNotAvailableStored() {
        return ResponseEntity.ok(storeCRUDService.getListDtoNotExistingInStockReport());
    }

    @PostMapping("/set-alias")
    public ResponseEntity<StoreResponse> setStoreAlias(@RequestBody StoreAliasDTO storeAliasDTO) {
        storeCRUDService.updateAlias(storeAliasDTO.getId(),storeAliasDTO.getAlias());
        return ResponseEntity.ok(getStoresResponse());
    }

    @PostMapping("/set-order")
    public ResponseEntity<StoreResponse> setStoreOrder(@RequestBody List<UUID> uuidList) {
        storeCRUDService.setIndexOrderInReport(uuidList);
        return ResponseEntity.ok(getStoresResponse());
    }


    private StoreResponse getStoresResponse() {
        StoreResponse storeResponse = new StoreResponse();
        storeResponse.setListDtoExistingInStockReport(storeCRUDService.getListDtoExistingInStockReport());
        storeResponse.setListDtoNotExistingInStockReport(storeCRUDService.getListDtoNotExistingInStockReport());
        return storeResponse;
    }



}
