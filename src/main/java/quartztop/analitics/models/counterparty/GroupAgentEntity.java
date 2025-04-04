package quartztop.analitics.models.counterparty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "agent_groups")
public class GroupAgentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String tag;

    @ManyToMany(mappedBy = "groupAgentEntityList") // Указываем, что связь уже описана в `GroupAgentEntity`
    private List<AgentEntity> agentEntityList;
}
