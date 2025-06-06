package quartztop.analitics.reports.salesReportToExcel;

import java.time.LocalDate;

public interface SalesReportDTO {
    LocalDate getMonth();
    String getOrganizationName();
    String getCategoryName();
    Float getTotalQuantity();
    Float getTotalSum();
}
