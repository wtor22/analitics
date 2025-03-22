package quartztop.analitics.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.products.BundleEntity;

import java.util.UUID;

@Repository
public interface BundleRepository extends JpaRepository<BundleEntity, UUID> {
}
