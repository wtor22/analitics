package quartztop.analitics.dtos.organizationData.store;

import lombok.Data;

import java.util.UUID;

@Data
public class StoreDto {
    private UUID id; //  внешний id из API
    private String name;
    private String description;
    private String nameToBot;
    private boolean existsInStockReport;
}
