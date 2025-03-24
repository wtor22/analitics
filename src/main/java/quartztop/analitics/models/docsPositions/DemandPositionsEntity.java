package quartztop.analitics.models.docsPositions;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.docs.DemandEntity;
import quartztop.analitics.models.products.BundleEntity;
import quartztop.analitics.models.products.ProductsEntity;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "demand_positions")
public class DemandPositionsEntity {

    @Id
    private UUID id; //  внешний id из API

    private float quantity;
    private int price; // Сумма в копейках
    private float discount;
    @Column(name = "type_product")
    private String type;

    @ManyToOne
    @JoinColumn(name = "demand_id")
    private DemandEntity demandEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductsEntity products;

    @ManyToOne
    @JoinColumn(name = "bundle_id")
    private BundleEntity bundle;

}
