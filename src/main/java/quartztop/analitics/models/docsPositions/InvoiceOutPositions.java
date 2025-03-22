package quartztop.analitics.models.docsPositions;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.docs.InvoiceOutEntity;
import quartztop.analitics.models.products.BundleEntity;
import quartztop.analitics.models.products.ProductsEntity;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "invoice_positions")
public class InvoiceOutPositions {

    @Id
    private UUID id; //  внешний id из API

    private float quantity;
    private int sum; // Сумма в копейках
    private float discount;
    @Column(name = "type_product")
    private String type;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private InvoiceOutEntity invoiceOutEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductsEntity products;

    @ManyToOne
    @JoinColumn(name = "bundle_id")
    private BundleEntity bundle;
}
