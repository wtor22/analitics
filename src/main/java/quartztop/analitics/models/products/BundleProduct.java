package quartztop.analitics.models.products;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "bundle_products")
public class BundleProduct {

    @EmbeddedId
    private BundleProductId id = new BundleProductId();

    @ManyToOne
    @MapsId("bundleId")
    @JoinColumn(name = "bundle_id")
    private BundleEntity bundle;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private ProductsEntity product;

    @Column(nullable = false)
    private double quantity;


}
