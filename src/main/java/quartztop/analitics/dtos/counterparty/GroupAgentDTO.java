package quartztop.analitics.dtos.counterparty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupAgentDTO {

    private int id;
    private String tag;
    private AgentDTO agentDTO;
}
