package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.services.crudDocs.DemandCRUDService;
import quartztop.analitics.handlers.DemandHandler;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/client/demand")
@RequiredArgsConstructor
@Slf4j
public class DemandClientController {

    private final MySkladClient httpClientSender;
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

    @GetMapping
    public ResponseEntity<String> getDemandListForYesterday(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        //log.error("Start controller");
        String response = demandHandler.downloadDemandsWithOffset(start, end);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DemandDTO> deleteDemandById(@PathVariable UUID id) {
        demandCRUDService.deleteDemandById(id);
        return ResponseEntity.noContent().build();
    }
}
