package quartztop.analitics.models.organizationData;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "owners")
public class OwnerEntity {

    @Id
    private UUID id; //  внешний id из API

    private String email; // Email сотрудника в МС
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    private String name;
    private String uid; // Логин сотрудника

}
