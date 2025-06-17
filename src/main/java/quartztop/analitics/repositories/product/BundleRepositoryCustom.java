package quartztop.analitics.repositories.product;

import quartztop.analitics.dtos.products.BundleDTO;

import java.util.List;
import java.util.UUID;

public interface BundleRepositoryCustom {
    List<UUID> findIdsWithUpdatedChanged(List<BundleDTO> incomingDtoList);
}
