package quartztop.analitics.models.organizationData;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "store")
public class StoreEntity {

    @Id
    private UUID id; //  внешний id из API
    private String name;
    @Column(length = 4096)
    private String description;
    private String nameToBot;
    private Integer orderInBotIndex;
}
