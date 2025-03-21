package quartztop.analitics.services.Initializers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import quartztop.analitics.configs.usersInitConfig.RolesInit;
import quartztop.analitics.configs.usersInitConfig.RolesInitList;
import quartztop.analitics.configs.usersInitConfig.UserInit;
import quartztop.analitics.configs.usersInitConfig.UserInitList;
import quartztop.analitics.dtos.users.UserDTO;
import quartztop.analitics.dtos.users.UsersRolesDTO;
import quartztop.analitics.models.users.UsersRoles;
import quartztop.analitics.repositories.user.UserRepositories;
import quartztop.analitics.repositories.user.UsersRolesRepositories;
import quartztop.analitics.services.crud.user.UserCRUDService;
import quartztop.analitics.services.crud.user.UsersRolesCRUDServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartInitService {
    private final UsersRolesRepositories usersRolesRepositories;
    private final UserRepositories userRepositories;
    private final UsersRolesCRUDServices usersRolesCRUDServices;
    private final UserCRUDService userCRUDService;
    private final RolesInitList rolesInitList;
    private final UserInitList userInitList;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void rolesStartInit() {

        if(usersRolesRepositories.count() == 0) {
            List<UsersRolesDTO> usersRolesDTOList = new ArrayList<>();
            List<RolesInit> rolesInites = rolesInitList.getUsersRoles();
            for (RolesInit rolesInit: rolesInites) {

                UsersRolesDTO usersRolesDTO = new UsersRolesDTO();
                usersRolesDTO.setNameRole(rolesInit.getNameRole());
                usersRolesDTO.setRole(rolesInit.getRole());
                usersRolesDTOList.add(usersRolesDTO);
            }
            usersRolesCRUDServices.createListUsersRoles(usersRolesDTOList);
            log.info("Created {} usersRoles" , usersRolesDTOList.size());
        }
        if(userRepositories.count() == 0) {

            List<UserInit> userInits = userInitList.getUsersInitList();
            List<UserDTO> userDTOList = new ArrayList<>();

            for (UserInit userInit: userInits) {
                UserDTO userDTO = new UserDTO();
                userDTO.setName(userInit.getName());
                userDTO.setSurName(userInit.getSurName());
                userDTO.setLastName(userInit.getLastNName());
                userDTO.setEmail(userInit.getEmail());
                userDTO.setPassword(passwordEncoder.encode("1111"));

                Optional<UsersRoles> optionalUsersRolesDTO =
                        usersRolesCRUDServices.getOptionalUsersRolesByRole(userInit.getRole());
                if (optionalUsersRolesDTO.isEmpty()) {
                    log.error("ROLE {} NOT FOUND IN DB", userInit.getRole());
                } else {
                    UsersRolesDTO usersRolesDTO = UsersRolesCRUDServices.mapToDto(optionalUsersRolesDTO.get());
                    userDTO.setUsersRolesDTO(usersRolesDTO);
                }
                userDTOList.add(userDTO);
            }
            userCRUDService.createListUsers(userDTOList);
            log.info("Created {} users" , userDTOList.size());
        }

    }
}
