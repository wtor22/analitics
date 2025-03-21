package quartztop.analitics.repositories.counterparty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.counterparty.ContractEntity;

import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, UUID> {
}
