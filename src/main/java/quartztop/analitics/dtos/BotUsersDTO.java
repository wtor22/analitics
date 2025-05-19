package quartztop.analitics.dtos;

import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.utils.Region;

import java.time.LocalDateTime;

@Setter
@Getter
public class BotUsersDTO {

    private Long telegramId;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDateTime registeredAt;
}
