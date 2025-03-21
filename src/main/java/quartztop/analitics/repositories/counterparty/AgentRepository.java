package quartztop.analitics.repositories.counterparty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.counterparty.AgentEntity;

import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<AgentEntity, UUID> {
}
