package quartztop.analitics.reports;

import lombok.Data;
import quartztop.analitics.dtos.products.CategoryDTO;

@Data
public class CategoryToGroup {

    private CategoryDTO categoryDTO;
    private double currentPeriodCount;
    private double comparePeriodCount;
    private double currentPeriodSum;
    private double comparePeriodSum;

}
