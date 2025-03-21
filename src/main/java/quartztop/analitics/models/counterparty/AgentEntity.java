package quartztop.analitics.models.counterparty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.organizationData.OwnerEntity;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "agents")
public class AgentEntity {

    @Id
    private UUID id; //  внешний id из API

    private String name;
    private String inn;
    private String legalAddress;
    private String legalFirstName; // Для физ лиц
    private String legalLastName; // Для физ лиц
    private String legalMiddleName; // Для физ лиц
    private String legalTitle; // Для юр лиц

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private OwnerEntity ownerEntity;

}
