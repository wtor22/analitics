package quartztop.analitics.dtos.counterparty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentWrapper {
    private List<AgentDTO> rows;
}
