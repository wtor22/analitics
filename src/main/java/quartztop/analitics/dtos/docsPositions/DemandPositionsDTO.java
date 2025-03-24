package quartztop.analitics.dtos.docsPositions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем все неизвестные поля
public class DemandPositionsDTO {

    private UUID id; //  внешний id из API

    private float quantity;
    private int price; // Сумма в копейках
    private float discount;
    private String type;

    @JsonDeserialize(using = AssortmentDeserializer.class)
    private Object assortment; // Тут может быть либо ProductDTO, либо BundleDTO


    @JsonIgnore
    private DemandDTO demandDTO;

    @JsonIgnore
    private ProductDTO productDTO;

    @JsonIgnore
    private BundleDTO bundle;
}
