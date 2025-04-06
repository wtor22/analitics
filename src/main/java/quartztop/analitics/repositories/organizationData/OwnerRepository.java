package quartztop.analitics.repositories.organizationData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.organizationData.OwnerEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<OwnerEntity, UUID> {

    public List<OwnerEntity> findAllByUsedInReportsTrue();
}
