package quartztop.analitics.dtos.counterparty;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import quartztop.analitics.dtos.organizationData.OwnerDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AgentDTO {

    private UUID id; //  внешний id из API

    private String name;
    private String inn;
    private String legalAddress;
    private String legalFirstName; // Для физ лиц
    private String legalLastName; // Для физ лиц
    private String legalMiddleName; // Для физ лиц
    private String legalTitle; // Для юр лиц
    private String[] tags; // Назначенные группы контрагента в МС
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updated;
    private OwnerDTO owner;
}
