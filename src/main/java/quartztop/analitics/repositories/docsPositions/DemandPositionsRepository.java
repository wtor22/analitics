package quartztop.analitics.repositories.docsPositions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.docsPositions.DemandPositionsEntity;

import java.util.UUID;

@Repository
public interface DemandPositionsRepository extends JpaRepository<DemandPositionsEntity, UUID> {
}
