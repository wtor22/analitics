package quartztop.analitics.dtos.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import quartztop.analitics.dtos.organizationData.CountriesDTO;

import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем все неизвестные поля
public class BundleDTO {
    private UUID id;
    private String article;
    private String code;
    private String description;
    private String name;
    private String pathName;

    private CountriesDTO country;

    @JsonProperty("components")  // Указываем путь до корневого объекта
    @JsonDeserialize(using = ProductListDeserializer.class)
    private List<ProductDTO> productDTOList;


}
