package quartztop.analitics.responses.categoryResponse;

import lombok.Data;
import quartztop.analitics.dtos.products.CategoryDTO;

import java.util.List;

@Data
public class CategoryResponse {
    List<CategoryDTO> categoriesIsPresent;
    List<CategoryDTO> categoriesNotPresent;
}
