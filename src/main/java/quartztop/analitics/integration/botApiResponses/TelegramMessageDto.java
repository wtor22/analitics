package quartztop.analitics.integration.botApiResponses;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramMessageDto {
    private String username;
    private String text;
}
