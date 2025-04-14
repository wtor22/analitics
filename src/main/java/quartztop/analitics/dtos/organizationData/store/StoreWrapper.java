package quartztop.analitics.dtos.organizationData.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreWrapper {
    private List<StoreDto> rows;
}
