package quartztop.analitics.models.counterparty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.counterparty.AgentEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "contracts")
public class ContractEntity {

    @Id
    private UUID id; //  внешний id из API

    private String name;
    private LocalDateTime moment;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private AgentEntity agentEntity;

}
