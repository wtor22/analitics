package quartztop.analitics.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.products.ProductAttributeEntity;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttributeEntity, Long> {
}
