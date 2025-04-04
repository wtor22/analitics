package quartztop.analitics.dtos.counterparty;

import lombok.Data;
import quartztop.analitics.dtos.organizationData.OwnerDTO;

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
    private OwnerDTO owner;
}
