package quartztop.analitics.services.crud.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.users.UserDTO;
import quartztop.analitics.dtos.users.UsersRolesDTO;
import quartztop.analitics.models.users.UserEntity;
import quartztop.analitics.models.users.UsersRoles;
import quartztop.analitics.repositories.user.UserRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCRUDService {

    private final UserRepositories userRepositories;
    private final UsersRolesCRUDServices usersRolesCRUDServices;

    public void createUser(UserDTO userDTO) {
        UserEntity userEntity = mapToEntity(userDTO);
        setRole(userEntity, userDTO.getUsersRolesDTO());
        userRepositories.save(userEntity);
    }

    public void createListUsers(List<UserDTO> userDTOList) {
        List<UserEntity> userEntityList = new ArrayList<>();
        for(UserDTO userDTO: userDTOList) {
            UserEntity userEntity = mapToEntity(userDTO);
            setRole(userEntity,userDTO.getUsersRolesDTO());
            userEntityList.add(userEntity);
        }
        userRepositories.saveAll(userEntityList);
    }

    /**
     * Устанавливает роль пользователю. Если роли не существует в базе, создаёт новую.
     * @param userEntity    объект пользователя, которому назначается роль.
     * @param usersRolesDTO объект с данными о роли.
     */
    private void setRole(UserEntity userEntity, UsersRolesDTO usersRolesDTO) {
        Optional<UsersRoles> optionalUsersRoles = usersRolesCRUDServices.getOptionalUsersRolesByRole(usersRolesDTO.getRole());
        if (optionalUsersRoles.isEmpty()) {
            userEntity.setUsersRoles(usersRolesCRUDServices.createUsersRoles(usersRolesDTO));
        } else {
            userEntity.setUsersRoles(optionalUsersRoles.get());
        }
    }

    public UserDTO getUserDtoByEmail(String email) {

        UserEntity userEntity = userRepositories.findByEmail(email);

        UsersRolesDTO usersRolesDTO = UsersRolesCRUDServices.mapToDto(userEntity.getUsersRoles());
        UserDTO userDTO = mapToDto(userEntity);
        userDTO.setUsersRolesDTO(usersRolesDTO);
        return userDTO;
    }

    public static UserDTO mapToDto(UserEntity userEntity) {

        UserDTO user = new UserDTO();
        user.setId(userEntity.getId());
        user.setName(userEntity.getName());
        user.setSurName(userEntity.getSurName());
        user.setLastName(userEntity.getLastName());
        user.setEmail(userEntity.getEmail());

        return user;
    }
    public static UserEntity mapToEntity(UserDTO userDTO) {

        UserEntity user = new UserEntity();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setSurName(userDTO.getSurName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        return user;
    }
}
