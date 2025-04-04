package quartztop.analitics.repositories.counterparty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.GroupAgentEntity;
import quartztop.analitics.models.docsPositions.DemandPositionsEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<AgentEntity, UUID> {

    @Query("SELECT a FROM AgentEntity a " +
            "JOIN a.groupAgentEntityList at " +
            "WHERE at = :groupAgent " +
            "ORDER BY a.name ASC")
    List<AgentEntity> findAllByGroupAgent(@Param("groupAgent") GroupAgentEntity groupAgent);
}
