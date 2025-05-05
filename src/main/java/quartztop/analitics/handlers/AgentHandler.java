package quartztop.analitics.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.services.counterparty.AgentCRUDService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentHandler {

    private final MySkladClient clientSender;
    private final AgentCRUDService agentCRUDService;

    private static boolean isDoIt = false;

    public String downloadListAgent() {

        if (isDoIt)  {
            return "ЖДИ!!! - Операция выполняется: загружаются данные ";
        }
        isDoIt = true;
        int offset = 0;
        int sizeAgentList;
        int countOperation = 0;
        int countUpdated = 0;

        List<AgentEntity> agentsFromDB = agentCRUDService.getAll();


        do {
            //log.warn("OFFSET " + offset + " SIZE AGENT LIST " + sizeAgentList + " COUNT AGENT " + countOperation);
            String offsetToString = String.valueOf(offset);
            List<AgentDTO> agentDTOList = clientSender.getListAgent(offsetToString);
            countUpdated = countUpdated + checkAndCreateAgents(agentsFromDB,agentDTOList);

            sizeAgentList = agentDTOList.size();
            countOperation = countOperation + sizeAgentList;
            offset = offset + 100;
        } while (sizeAgentList == 100);

        isDoIt = false;
        return "Операция завершена успешно, Количество обработанных контрагентов: " + countUpdated;
    }

    private int checkAndCreateAgents(List<AgentEntity> agentsFromDB, List<AgentDTO> agentDTOList) {
        Map<UUID, LocalDateTime> agentsMap = agentsFromDB.stream()
                .filter(agent -> agent.getUpdated() != null)
                .collect(Collectors.toMap(AgentEntity::getId, AgentEntity::getUpdated));

        List<AgentDTO> doUpdatedList = agentDTOList.stream().filter(
                agentDTO -> {
                    LocalDateTime updated = agentsMap.get(agentDTO.getId());
                    return !Objects.equals(updated,agentDTO.getUpdated());
                }
        ).toList();

        agentCRUDService.createAll(doUpdatedList);
        return doUpdatedList.size();
    }
}
