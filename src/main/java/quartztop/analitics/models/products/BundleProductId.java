package quartztop.analitics.models.products;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class BundleProductId implements Serializable {
    private UUID bundleId;
    private UUID productId;

}
