package quartztop.analitics.models.organizationData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.actions.ActionEntity;
import quartztop.analitics.models.counterparty.AgentEntity;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    private UUID id; //  внешний id из API
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "actionOrganizationList") // Указываем, что связь уже описана в `GroupAgentEntity`
    private List<ActionEntity> actionList;
}
