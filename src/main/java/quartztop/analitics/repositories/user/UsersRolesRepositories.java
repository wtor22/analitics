package quartztop.analitics.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.users.UsersRoles;

import java.util.Optional;

@Repository
public interface UsersRolesRepositories extends JpaRepository<UsersRoles, Integer> {



    Optional<UsersRoles> findByRole(String role);
}
