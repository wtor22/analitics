package quartztop.analitics.dtos.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем все неизвестные поля
public class ProductAttributeDTO {
    private String name;
    private String type;
    private String value; // Стринга, потом сконвертируется как надо (Надеюсь)
}
