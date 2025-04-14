package quartztop.analitics.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import quartztop.analitics.models.products.CategoryEntity;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    public   List<CategoryEntity> findAllByUsedInReportsTrue();

    @Query("SELECT c FROM CategoryEntity c WHERE c.orderInBotIndex > 0  ORDER BY c.orderInBotIndex ASC")
    List<CategoryEntity> findAllOrdered();

    //List<CategoryEntity> findAllByOrderInBotIndexIsNull();

    @Query("SELECT c FROM CategoryEntity c WHERE c.orderInBotIndex IS NULL OR c.orderInBotIndex = 0")
    List<CategoryEntity> findAllWithNullOrZeroOrder();

}
