package quartztop.analitics.dtos.docsPositions;

import lombok.Data;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.models.products.BundleEntity;

import java.util.UUID;

@Data
public class DemandPositionsDTO {

    private UUID id; //  внешний id из API

    private float quantity;
    private int sum; // Сумма в копейках
    private float discount;
    private String type;

    private DemandDTO demandDTO;

    private ProductDTO productDTO;

    private BundleDTO bundle;
}
