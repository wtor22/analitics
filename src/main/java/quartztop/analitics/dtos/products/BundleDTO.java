package quartztop.analitics.dtos.products;

import lombok.Data;
import quartztop.analitics.dtos.organizationData.CountriesDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class BundleDTO {
    private UUID id;
    private String article;
    private String code;
    private String description;
    private String name;
    private String pathName;

    private CountriesDTO countriesDTO;

    private List<ProductDTO> productDTOList = new ArrayList<>();
}
