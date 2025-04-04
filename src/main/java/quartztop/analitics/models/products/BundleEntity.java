package quartztop.analitics.models.products;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.organizationData.CountriesEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "bundles")
public class BundleEntity {

    @Id
    private UUID id; //  внешний id из API

    private String article;
    private String code;
    private String description;
    private String name;
    private String pathName; // Наименование группы, в которую входит Товар

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BundleProduct> bundleProducts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    private CountriesEntity countries;

    // Добавляем метод для добавления продуктов
    public void addProduct(ProductsEntity product, double quantity) {
        BundleProduct bundleProduct = new BundleProduct();
        bundleProduct.setBundle(this);
        bundleProduct.setProduct(product);
        bundleProduct.setQuantity(quantity);
        this.bundleProducts.add(bundleProduct);
    }
}
