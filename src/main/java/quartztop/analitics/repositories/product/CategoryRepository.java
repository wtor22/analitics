package quartztop.analitics.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import quartztop.analitics.models.products.CategoryEntity;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    public   List<CategoryEntity> findAllByUsedInReportsTrue();
}
