package quartztop.analitics.services.counterparty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.counterparty.GroupAgentDTO;
import quartztop.analitics.models.counterparty.GroupAgentEntity;
import quartztop.analitics.repositories.counterparty.GroupAgentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupAgentCRUDService {
    private final GroupAgentRepository groupAgentRepository;

    public GroupAgentEntity getEntity(int id) {
        return groupAgentRepository.findById(id).orElse(null);
    }

    public static GroupAgentDTO mapToDTO(GroupAgentEntity groupAgentEntity) {
        GroupAgentDTO groupAgentDTO = new GroupAgentDTO();
        groupAgentDTO.setId(groupAgentEntity.getId());
        groupAgentDTO.setTag(groupAgentEntity.getTag());
        return groupAgentDTO;
    }
}
