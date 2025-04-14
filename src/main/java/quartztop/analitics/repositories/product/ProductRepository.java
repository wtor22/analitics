package quartztop.analitics.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.products.ProductsEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductsEntity, UUID> {

    @Query("SELECT id FROM ProductsEntity WHERE id IN :ids")
    List<UUID> findExistingIds(@Param("ids") List<UUID> ids);

    @Query("SELECT p FROM ProductsEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.article) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<ProductsEntity> findExistingIdsBySearch(@Param("search") String search);
}
