package quartztop.analitics.models.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users_roles")
public class UsersRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String role;
    String nameRole;
}
