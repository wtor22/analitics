package quartztop.analitics.dtos.organizationData;

import lombok.Data;

import java.util.UUID;

@Data
public class CountriesDTO {
    private UUID id; //  внешний id из API
    private String code;
    private String name;
}
