package quartztop.analitics.integration.botApiResponses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ButtonDto {

    private Long id;
    private String textButton;
    private String buttonValue;
    private Integer orderInBotIndex;
}
