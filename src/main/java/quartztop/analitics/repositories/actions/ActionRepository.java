package quartztop.analitics.repositories.actions;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.actions.ActionEntity;
import quartztop.analitics.models.organizationData.Organization;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<ActionEntity, Long> {

    Optional<ActionEntity> findByName(String name);

    Optional<ActionEntity> findFirstByOrderByIdAsc();
    Optional<ActionEntity> findFirstByIdGreaterThanOrderByIdAsc(Long currentId);


    @Query("""
    SELECT a FROM ActionEntity a
    JOIN a.actionOrganizationList org
    WHERE (:currentId IS NULL OR a.id > :currentId)
        AND a.isActive = true
        AND org = :organization
        AND (
            (a.startActionDate IS NULL OR a.startActionDate <= :now)
            AND (a.endActionDate IS NULL OR a.endActionDate >= :now)
        )
    ORDER BY a.id ASC
""")
    List<ActionEntity> findNextAction(
            @Param("currentId") Long currentId,
            @Param("now") LocalDate now,
            @Param("organization") Organization organization,
            Pageable pageable
    );
}
