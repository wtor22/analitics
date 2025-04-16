package quartztop.analitics.repositories.organizationData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.organizationData.StoreEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {

    @Query("SELECT c FROM StoreEntity c WHERE c.orderInBotIndex > 0  ORDER BY c.orderInBotIndex ASC")
    List<StoreEntity> findAllOrdered();

    @Query("SELECT c FROM StoreEntity c WHERE c.orderInBotIndex IS NULL OR c.orderInBotIndex = 0")
    List<StoreEntity> findAllNotOrdered();


}
