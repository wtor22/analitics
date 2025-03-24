package quartztop.analitics.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.httpClient.OkHttpClientSender;
import quartztop.analitics.services.crudDocs.DemandCRUDService;
import quartztop.analitics.services.handlers.DemandHandler;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/client/demand")
@RequiredArgsConstructor
@Slf4j
public class DemandClientController {

    private final OkHttpClientSender httpClientSender;
    private final DemandCRUDService demandCRUDService;
    private final DemandHandler demandHandler;

    @GetMapping("/{id}")
    public ResponseEntity<DemandDTO> demandById(@PathVariable UUID id) {
        DemandDTO demandDTO = httpClientSender.getDemand(id);
        demandHandler.prepareDemand(demandDTO);
        if (demandDTO.getDeleted() != null) {
            demandCRUDService.deleteDemandById(id);
            return ResponseEntity.noContent().build();
        }
        demandCRUDService.create(demandDTO);
        return ResponseEntity.ok(demandDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DemandDTO> deleteDemandById(@PathVariable UUID id) {
        log.info("START DELETING");
        demandCRUDService.deleteDemandById(id);
        return ResponseEntity.noContent().build();
    }

}
