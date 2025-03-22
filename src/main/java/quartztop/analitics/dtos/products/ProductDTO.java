package quartztop.analitics.dtos.products;

import jakarta.persistence.ManyToMany;
import lombok.Data;
import quartztop.analitics.dtos.organizationData.CountriesDTO;
import quartztop.analitics.models.products.BundleEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ProductDTO {

    private UUID id; //  внешний id из API

    private String article;
    private String code;
    private String description;
    private String name;
    private String pathName; // Наименование группы, в которую входит Товар

    private CountriesDTO countriesDTO;

    private List<BundleDTO> bundleDTOList = new ArrayList<>();
}
