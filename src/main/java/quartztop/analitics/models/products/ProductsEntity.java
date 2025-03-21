package quartztop.analitics.models.products;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.organizationData.Countries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "products")
public class ProductsEntity {

    @Id
    private UUID id; //  внешний id из API

    private String article;
    private String code;
    private String description;
    private String name;
    private String pathName; // Наименование группы, в которую входит Товар

    @ManyToOne
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    private Countries countries;

    @ManyToMany(mappedBy = "productsList") // Указываем, что связь уже описана в `Bundles`
    private List<Bundles> bundlesList = new ArrayList<>();
}
