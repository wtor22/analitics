package quartztop.analitics.services.counterparty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.dtos.counterparty.GroupAgentDTO;
import quartztop.analitics.dtos.organizationData.OwnerDTO;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.GroupAgentEntity;
import quartztop.analitics.models.organizationData.OwnerEntity;
import quartztop.analitics.repositories.counterparty.AgentRepository;
import quartztop.analitics.repositories.counterparty.GroupAgentRepository;
import quartztop.analitics.services.crudOrganization.OwnerCRUDService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentCRUDService {
    private final AgentRepository agentRepository;
    private final GroupAgentRepository groupAgentRepository;
    private final OwnerCRUDService ownerCRUDService;

    @Transactional
    public AgentEntity create(AgentDTO agentDTO) {
        AgentEntity agentEntity = mapToEntity(agentDTO);
        setOwner(agentEntity,agentDTO.getOwner());
        if(agentDTO.getTags() != null) setGroups(agentEntity, agentDTO.getTags());

        return agentRepository.save(agentEntity);
    }

    public Optional<AgentEntity> getOptionalEntity(AgentDTO agentDTO) {
        return  agentRepository.findById(agentDTO.getId());
    }

    public List<AgentEntity> getListEntityByTag(GroupAgentEntity groupAgentEntity) {
        return agentRepository.findAllByGroupAgent(groupAgentEntity);
    }


    public List<GroupAgentDTO> getGroupByManager(OwnerEntity ownerEntity) {
        List<GroupAgentEntity> groupAgentEntityList = groupAgentRepository.findGroupsByOwner(ownerEntity);
        return groupAgentEntityList.stream().map(GroupAgentCRUDService::mapToDTO).toList();
    }

    private void setGroups(AgentEntity agent, String[] arrayTags) {
        for(int i = 0; i < arrayTags.length; i++) {

            GroupAgentEntity groupAgentEntity = groupAgentRepository.findByTag(arrayTags[i]);
            if(groupAgentEntity != null && !agent.getGroupAgentEntityList().contains(groupAgentEntity)) {
                agent.getGroupAgentEntityList().add(groupAgentEntity);
                continue;
            }
            if (groupAgentEntity == null) {
                GroupAgentEntity createdGroupAgentEntity = new GroupAgentEntity();
                createdGroupAgentEntity.setTag(arrayTags[i]);
                agent.getGroupAgentEntityList().add(groupAgentRepository.save(createdGroupAgentEntity));
            }
        }
    }

    private void setOwner(AgentEntity agentEntity, OwnerDTO ownerDTO) {
        Optional<OwnerEntity> optionalOwnerEntity = ownerCRUDService.getOptionalEntity(ownerDTO);
        if(optionalOwnerEntity.isEmpty()) {
            log.info("Owner {} NOT FOUND", ownerDTO.getFullName());
            OwnerEntity ownerEntity = ownerCRUDService.create(ownerDTO);
            log.info("Owner {} was created", ownerEntity.getFullName());
            agentEntity.setOwnerEntity(ownerEntity);
        } else {
            agentEntity.setOwnerEntity(optionalOwnerEntity.get());
        }
    }


    public static AgentEntity mapToEntity(AgentDTO agent) {

        AgentEntity agentEntity = new AgentEntity();
        agentEntity.setId(agent.getId());
        agentEntity.setName(agent.getName());
        agentEntity.setLegalTitle(agent.getLegalTitle());
        agentEntity.setInn(agent.getInn());
        agentEntity.setLegalAddress(agent.getLegalAddress());
        agentEntity.setLegalFirstName(agent.getLegalFirstName());
        agentEntity.setLegalLastName(agent.getLegalLastName());
        agentEntity.setLegalMiddleName(agent.getLegalMiddleName());
        return agentEntity;
    }
    public static AgentDTO mapToDTO(AgentEntity agent) {

        AgentDTO agentDTO = new AgentDTO();
        agentDTO.setId(agent.getId());
        agentDTO.setName(agent.getName());
        agentDTO.setLegalTitle(agent.getLegalTitle());
        agentDTO.setInn(agent.getInn());
        agentDTO.setLegalAddress(agent.getLegalAddress());
        agentDTO.setLegalFirstName(agent.getLegalFirstName());
        agentDTO.setLegalLastName(agent.getLegalLastName());
        agentDTO.setLegalMiddleName(agent.getLegalMiddleName());
        return agentDTO;
    }
}
