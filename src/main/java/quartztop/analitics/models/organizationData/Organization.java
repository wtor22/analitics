package quartztop.analitics.models.organizationData;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    private UUID id; //  внешний id из API

    private String name;
}
