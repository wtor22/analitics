package quartztop.analitics.models.reports;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.organizationData.StoreEntity;
import quartztop.analitics.models.products.ProductsEntity;

import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "stock_by_store")
public class StockByStoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float stock;

    @Column(name = "in_transit")
    private float inTransit;

    private float reserve;

    @Column(name = "store_name")
    private String storeName;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreEntity storeEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductsEntity productsEntity;

}
