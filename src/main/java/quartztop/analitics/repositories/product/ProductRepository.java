package quartztop.analitics.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.products.ProductsEntity;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductsEntity, UUID> {
}
