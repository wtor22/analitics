package quartztop.analitics.services.crud.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.users.UsersRolesDTO;
import quartztop.analitics.models.users.UsersRoles;
import quartztop.analitics.repositories.user.UsersRolesRepositories;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersRolesCRUDServices {

    private final UsersRolesRepositories usersRolesRepositories;

    public UsersRolesDTO getUsersRolesDTOByRole(int roleId) {
        UsersRoles usersRoles = usersRolesRepositories.findById(roleId).orElseThrow();
        return mapToDto(usersRoles);
    }

    public Optional<UsersRoles> getOptionalUsersRolesByRole(String role) {
        return usersRolesRepositories.findByRole(role);
    }

    public UsersRoles createUsersRoles(UsersRolesDTO usersRolesDTO) {

        return usersRolesRepositories.save(mapToEntity(usersRolesDTO));
    }

    public void createListUsersRoles(List<UsersRolesDTO> usersRolesDTOList) {
        for (UsersRolesDTO usersRolesDTO: usersRolesDTOList) {
            Optional<UsersRoles> optionalUsersRoles = getOptionalUsersRolesByRole(usersRolesDTO.getRole());
            if(optionalUsersRoles.isEmpty()) usersRolesRepositories.save(mapToEntity(usersRolesDTO));
        }

    }

    public static UsersRolesDTO mapToDto(UsersRoles usersRoles) {

        UsersRolesDTO usersRolesDTO = new UsersRolesDTO();
        usersRolesDTO.setNameRole(usersRoles.getNameRole());
        usersRolesDTO.setRole(usersRoles.getRole());
        usersRolesDTO.setId(usersRoles.getId());
        return usersRolesDTO;
    }

    public static UsersRoles mapToEntity(UsersRolesDTO usersRolesDTO) {

        UsersRoles usersRoles = new UsersRoles();
        usersRoles.setNameRole(usersRolesDTO.getNameRole());
        usersRoles.setRole(usersRolesDTO.getRole());
        return usersRoles;
    }

}
