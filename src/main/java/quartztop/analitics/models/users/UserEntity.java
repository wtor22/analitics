package quartztop.analitics.models.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "e_mail", unique = true)
    private String email;

    private String name;
    private String surName;
    private String LastName;
    private String password;

    @ManyToOne
    @JoinColumn(name = "role")
    private UsersRoles usersRoles;
}
