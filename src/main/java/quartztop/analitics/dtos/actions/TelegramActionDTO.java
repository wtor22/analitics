package quartztop.analitics.dtos.actions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class TelegramActionDTO {

    private String name;
    private String description;
    private String titleImageUrl;
    private Long id;

}
