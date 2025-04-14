package quartztop.analitics.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {
    private String href;
    private String metadataHref;
    private String type;
    private String mediaType;
}
