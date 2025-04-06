package quartztop.analitics.dtos.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем все неизвестные поля
public class CategoryDTO {

    private UUID id;
    private String code;
    private String description;
    private String name;
    private String pathName;
    private boolean usedInReports;
}
