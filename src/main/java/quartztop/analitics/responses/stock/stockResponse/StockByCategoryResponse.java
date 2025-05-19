package quartztop.analitics.responses.stock.stockResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockByCategoryResponse {
    private String category;
    private int order;
    List<StockByProductResponse> productsList;
}
