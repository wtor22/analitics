package quartztop.analitics.repositories.docs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.counterparty.GroupAgentEntity;
import quartztop.analitics.models.docs.DemandEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DemandRepository extends JpaRepository<DemandEntity, UUID> {


    public LocalDateTime findUpdatedById(UUID id);

    @Query("SELECT d.updated FROM DemandEntity d WHERE d.id = :id")
    LocalDateTime findMomentById(@Param("id") UUID id);

}
