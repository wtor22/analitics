package quartztop.analitics.repositories.organizationData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.organizationData.Organization;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    @Query("SELECT o.id FROM Organization o")
    List<UUID> findAllId();
}
