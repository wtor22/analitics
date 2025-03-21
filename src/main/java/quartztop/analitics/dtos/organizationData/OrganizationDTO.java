package quartztop.analitics.dtos.organizationData;

import lombok.Data;

import java.util.UUID;

@Data
public class OrganizationDTO {
    private UUID id; //  внешний id из API
    private String name;
}
