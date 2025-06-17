package quartztop.analitics.repositories.product;

import quartztop.analitics.dtos.products.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductRepositoryCustom {
    List<UUID> findIdsWithUpdatedChanged(List<ProductDTO> incomingDtoList);
}
