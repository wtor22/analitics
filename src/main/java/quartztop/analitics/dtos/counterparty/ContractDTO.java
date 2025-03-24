package quartztop.analitics.dtos.counterparty;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ContractDTO {
    private UUID id; //  внешний id из API
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime moment;
    private AgentDTO agent;
}
