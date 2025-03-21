package quartztop.analitics.dtos.counterparty;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ContractDTO {
    private UUID id; //  внешний id из API
    private String name;
    private LocalDateTime moment;
    private AgentDTO agent;
}
