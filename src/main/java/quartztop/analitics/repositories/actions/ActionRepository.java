package quartztop.analitics.repositories.actions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.actions.ActionEntity;

import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<ActionEntity, Long> {

    Optional<ActionEntity> findByName(String name);

    Optional<ActionEntity> findFirstByOrderByIdAsc();

    Optional<ActionEntity> findFirstByIdGreaterThanOrderByIdAsc(Long currentId);
}
