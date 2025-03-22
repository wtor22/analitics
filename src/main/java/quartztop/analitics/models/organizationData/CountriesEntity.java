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
@Table(name = "countries")
public class CountriesEntity {

    @Id
    private UUID id; //  внешний id из API

    private String code;
    private String name;

}
