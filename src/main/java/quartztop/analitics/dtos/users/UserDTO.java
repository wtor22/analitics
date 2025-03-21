package quartztop.analitics.dtos.users;

import lombok.Data;

@Data
public class UserDTO {
    int id;
    private String email;
    private String name;
    private String surName;
    private String LastName;
    private String password;
    private UsersRolesDTO usersRolesDTO;
}
