package quartztop.analitics.repositories.counterparty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.counterparty.GroupAgentEntity;
import quartztop.analitics.models.organizationData.OwnerEntity;

import java.util.List;

@Repository
public interface GroupAgentRepository extends JpaRepository<GroupAgentEntity, Integer> {

    GroupAgentEntity findByTag(String tag);

    @Query("SELECT DISTINCT g FROM GroupAgentEntity g " +
            "JOIN g.agentEntityList a " +
            "WHERE a.ownerEntity = :ownerEntity")
    List<GroupAgentEntity> findGroupsByOwner(@Param("ownerEntity") OwnerEntity ownerEntity);

}
