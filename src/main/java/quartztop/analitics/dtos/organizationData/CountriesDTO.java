package quartztop.analitics.dtos.organizationData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем все неизвестные поля
public class CountriesDTO {
    private UUID id; //  внешний id из API
    private String code;
    private String name;
}
