package quartztop.analitics.dtos.reports;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportStockByStoreWrapper {
    private List<StockReportRow> rows;
}
