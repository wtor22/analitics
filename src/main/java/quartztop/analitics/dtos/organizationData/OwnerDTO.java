package quartztop.analitics.dtos.organizationData;

import lombok.Data;

import java.util.UUID;

@Data
public class OwnerDTO {
    private UUID id; //  внешний id из API
    private String email; // Email сотрудника в МС
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    private String name;
    private String uid; // Логин сотрудника
}
