package quartztop.analitics.responses.stock.stockResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockByStoreResponse {

    private String nameStore;
    private String nameToBot;
    private float stock;
    private float reserve;
    private float inTransit;
}
