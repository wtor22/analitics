package quartztop.analitics.models.counterparty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.organizationData.OwnerEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private LocalDateTime updated;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private OwnerEntity ownerEntity;

    @ManyToMany
    @JoinTable(
            name = "agent_tag", // Название промежуточной таблицы
            joinColumns = @JoinColumn(name = "agent_id"),  // Связь с таблицей контрагентов
            inverseJoinColumns = @JoinColumn(name = "group_id") // Связь с таблицей групп
    )
    private List<GroupAgentEntity> groupAgentEntityList = new ArrayList<>();

}
