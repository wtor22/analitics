package quartztop.analitics.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import quartztop.analitics.dtos.counterparty.GroupAgentDTO;
import quartztop.analitics.dtos.organizationData.OwnerDTO;
import quartztop.analitics.models.organizationData.OwnerEntity;
import quartztop.analitics.services.counterparty.AgentCRUDService;
import quartztop.analitics.services.crudOrganization.OwnerCRUDService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/client/manager")
@RequiredArgsConstructor
@Slf4j
public class ManagerClientController {

    private final OwnerCRUDService ownerCRUDService;
    private final AgentCRUDService agentCRUDService;

    @GetMapping
    public ResponseEntity<List<OwnerDTO>> getListManagers() {
        List<OwnerDTO> ownerDTOList = ownerCRUDService.getListOwnersDTO();

        return ResponseEntity.ok(ownerDTOList);
    }

    @GetMapping("/tags")
    public ResponseEntity<List<GroupAgentDTO>> getTagsByManager(@RequestParam UUID managerId) {
        Optional<OwnerEntity> optionalOwnerEntity = ownerCRUDService.getOptionalEntity(managerId);
        return optionalOwnerEntity.map(ownerEntity -> ResponseEntity.ok(agentCRUDService.getGroupByManager(ownerEntity))).orElse(null);
    }


}
