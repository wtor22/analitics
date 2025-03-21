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
@Table(name = "bundles")
public class Bundles {

    @Id
    private UUID id; //  внешний id из API

    private String article;
    private String code;
    private String description;
    private String name;
    private String pathName; // Наименование группы, в которую входит Товар

    @ManyToMany
    @JoinTable(
            name = "bundle_products", // Название промежуточной таблицы
            joinColumns = @JoinColumn(name = "bundle_id"),  // Связь с таблицей комплектов
            inverseJoinColumns = @JoinColumn(name = "product_id") // Связь с таблицей товаров
    )
    private List<ProductsEntity> productsList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    private Countries countries;
}
