package quartztop.analitics.services.counterparty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.dtos.counterparty.ContractDTO;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.ContractEntity;
import quartztop.analitics.repositories.counterparty.ContractRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractCRUDService {

    private final ContractRepository contractRepository;
    private final AgentCRUDService agentCRUDService;

    @Transactional
    public ContractEntity create(ContractDTO contractDTO) {

        ContractEntity contractEntity = mapToEntity(contractDTO);
        setAgent(contractEntity,contractDTO.getAgent());
        return contractRepository.save(contractEntity);
    }
    public Optional<ContractEntity> getOptionalEntity(ContractDTO contractDTO) {
        return contractRepository.findById(contractDTO.getId());
    }

    public void setAgent(ContractEntity contractEntity, AgentDTO agentDTO) {

        Optional<AgentEntity>  optionalAgentEntity = agentCRUDService.getOptionalEntity(agentDTO);
        if (optionalAgentEntity.isEmpty()) {
            log.info("Agent {} NOT FOUND", agentDTO.getName());
            AgentEntity agentEntity = agentCRUDService.create(agentDTO);
            log.info("Agent {} was created", agentEntity.getName());
            contractEntity.setAgentEntity(agentEntity);
        } else {
            contractEntity.setAgentEntity(optionalAgentEntity.get());
        }
    }


    public static ContractEntity mapToEntity(ContractDTO contract) {

        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setId(contract.getId());
        contractEntity.setName(contract.getName());
        contractEntity.setMoment(contract.getMoment());
        return contractEntity;
    }

    public static ContractDTO mapToDTO(ContractEntity contract) {

        ContractDTO contractDTO = new ContractDTO();
        contractDTO.setId(contract.getId());
        contractDTO.setName(contract.getName());
        contractDTO.setMoment(contract.getMoment());
        return contractDTO;
    }
}
