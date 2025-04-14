package quartztop.analitics.repositories.reports;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.reports.StockByStoreEntity;

import java.util.List;

@Repository
public interface ReportsStockByStoreRepository extends JpaRepository<StockByStoreEntity, Long> {


    @Query("SELECT s FROM StockByStoreEntity s " +
            "JOIN s.productsEntity p " +
            "JOIN p.categoryEntity c " +
            "WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.article) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND c.orderInBotIndex > 0 ")
    List<StockByStoreEntity> getStockReportsBySearch(@Param("search") String search);
}
