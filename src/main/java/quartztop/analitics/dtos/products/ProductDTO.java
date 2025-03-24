package quartztop.analitics.dtos.products;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import quartztop.analitics.dtos.organizationData.CountriesDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем все неизвестные поля
public class ProductDTO {

    private UUID id; //  внешний id из API

    private String article;
    private String code;
    private String description;
    private String name;
    private String pathName; // Наименование группы, в которую входит Товар

    private CountriesDTO country;

    @JsonIgnore
    private List<BundleDTO> bundleDTOList = new ArrayList<>();
}
