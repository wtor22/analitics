package quartztop.analitics.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.users.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepositories extends JpaRepository<UserEntity, Integer> {
    UserEntity findByEmail(String email);
    Optional<UserEntity> findByEmailIgnoreCase(String email);
}
