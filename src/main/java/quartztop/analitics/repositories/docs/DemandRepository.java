package quartztop.analitics.repositories.docs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.docs.DemandEntity;

import java.util.UUID;

@Repository
public interface DemandRepository extends JpaRepository<DemandEntity, UUID> {
}
