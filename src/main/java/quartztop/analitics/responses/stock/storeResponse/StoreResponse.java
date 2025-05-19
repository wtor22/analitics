package quartztop.analitics.responses.stock.storeResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import quartztop.analitics.dtos.organizationData.store.StoreDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreResponse {

    private List<StoreDto> listDtoExistingInStockReport;
    private List<StoreDto> listDtoNotExistingInStockReport;


}
