package quartztop.analitics.dtos.products;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BundleWrapperDto {
    private List<BundleDTO> rows;
}
