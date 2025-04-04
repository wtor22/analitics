package quartztop.analitics.repositories.docsPositions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.GroupAgentEntity;
import quartztop.analitics.models.docsPositions.DemandPositionsEntity;
import quartztop.analitics.models.products.CategoryEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DemandPositionsRepository extends JpaRepository<DemandPositionsEntity, UUID> {

    @Query("SELECT dp FROM DemandPositionsEntity dp " +
            "JOIN dp.demandEntity d " +
            "JOIN d.agentEntity a " +
            "JOIN a.groupAgentEntityList gl " +
            "WHERE d.moment >= :periodStart AND d.moment <= :periodEnd " +
            "AND gl = :groupAgentEntity")
    List<DemandPositionsEntity> findAllByPeriodAndTags(@Param("periodStart") LocalDateTime periodStart,
                                              @Param("periodEnd") LocalDateTime periodEnd,
                                              @Param("groupAgentEntity") GroupAgentEntity groupAgentEntity);


    @Query("SELECT " +
            "COALESCE(" +
            "(SELECT SUM(dp.quantity) FROM DemandPositionsEntity dp " +
            "LEFT JOIN dp.products p " +
            "LEFT JOIN p.categoryEntity c " +
            "JOIN dp.demandEntity d " +
            "JOIN d.agentEntity a " +
            "WHERE d.moment >= :periodStart AND d.moment <= :periodEnd " +
            "AND c = :category " +
            "AND a = :agent " +
            "AND dp.products IS NOT NULL), 0) " +

            "+ COALESCE(" +
            "(SELECT SUM(dp.quantity * bp.quantity) FROM DemandPositionsEntity dp " +
            "LEFT JOIN dp.bundle b " +
            "LEFT JOIN b.bundleProducts bp " +
            "LEFT JOIN bp.product bp_product " +
            "LEFT JOIN bp_product.categoryEntity c " +
            "JOIN dp.demandEntity d " +
            "JOIN d.agentEntity a " +
            "WHERE d.moment >= :periodStart AND d.moment <= :periodEnd " +
            "AND c = :category " +
            "AND a = :agent " +
            "AND dp.bundle IS NOT NULL " +
            "AND bp_product IS NOT NULL), 0)")
    Double getQuantityProductByAgentAndCategory(@Param("periodStart") LocalDateTime periodStart,
                                                @Param("periodEnd") LocalDateTime periodEnd,
                                                @Param("category") CategoryEntity category,
                                                @Param("agent")AgentEntity agent);

    @Query("SELECT COALESCE( " +
            "    (SELECT " +
            "       SUM(" +
            "           (CASE " +
            "           WHEN dp.discount = 0 THEN dp.price * dp.quantity / 100 " +
            "           ELSE (dp.price -  dp.price * dp.discount / 100) /100 END" +
            "           )" +
            "       ) " +
            "     FROM DemandPositionsEntity dp " +
            "     LEFT JOIN dp.products p " +
            "     LEFT JOIN p.categoryEntity c " +
            "     JOIN dp.demandEntity d " +
            "     JOIN d.agentEntity a " +
            "     WHERE d.moment BETWEEN :periodStart AND :periodEnd " +
            "     AND c = :category " +
            "     AND a = :agent " +
            "     AND dp.products IS NOT NULL), 0) " +
            "+ COALESCE( " +
            "    (SELECT " +
            "       SUM(" +
            "           CASE WHEN dp.discount = 0 THEN  dp.price * dp.quantity / 100  " +
            "           ELSE (dp.price - dp.price * dp.discount / 100) /100 END " +
            "       ) " +
            "     FROM DemandPositionsEntity dp " +
            "     LEFT JOIN dp.bundle b " +
            "     LEFT JOIN b.bundleProducts bp " +
            "     LEFT JOIN bp.product bp_product " +
            "     LEFT JOIN bp_product.categoryEntity c " +
            "     JOIN dp.demandEntity d " +
            "     JOIN d.agentEntity a " +
            "     WHERE d.moment BETWEEN :periodStart AND :periodEnd " +
            "     AND c = :category " +
            "     AND a = :agent " +
            "     AND dp.bundle IS NOT NULL " +
            "     AND bp_product IS NOT NULL), 0) ")
    Double getSumPriceByAgentAndCategory(@Param("periodStart") LocalDateTime periodStart,
                                                @Param("periodEnd") LocalDateTime periodEnd,
                                                @Param("category") CategoryEntity category,
                                                @Param("agent")AgentEntity agent);





//    @Query("SELECT dp FROM DemandPositionsEntity dp " +
//            "LEFT JOIN dp.products p " +
//            "LEFT JOIN p.categoryEntity c " +
//            "JOIN dp.demandEntity d " +
//            "JOIN d.agentEntity a " +
//            "WHERE d.moment >= :periodStart AND d.moment <= :periodEnd " +
//            "AND c =: category " +
//            "AND a = :agent " +
//            "AND dp.products IS NOT NULL " +
//
//            "UNION " +
//
//            "SELECT dp FROM DemandPositionsEntity dp " +
//            "LEFT JOIN dp.bundle b " +
//            "LEFT JOIN b.bundleProducts bp " +
//            "LEFT JOIN bp.product bp_product " +
//            "LEFT JOIN bp_product.categoryEntity bc " +
//            "JOIN dp.demandEntity d " +
//            "JOIN d.agentEntity a " +
//            "WHERE d.moment >= :periodStart AND d.moment <= :periodEnd " +
//            "AND c =: category " +
//            "AND a = :agent " +
//            "AND dp.bundle IS NOT NULL")
//    List<DemandPositionsEntity> getListPositionEntityByAgentAndCategory(@Param("periodStart") LocalDateTime periodStart,
//                                                                        @Param("periodEnd") LocalDateTime periodEnd,
//                                                                        @Param("category") CategoryEntity category,
//                                                                        @Param("agent")AgentEntity agent);


    @Query("SELECT DISTINCT c FROM DemandPositionsEntity dp " +
            "LEFT JOIN dp.products p " +
            "LEFT JOIN p.categoryEntity c " +
            "JOIN dp.demandEntity d " +
            "JOIN d.agentEntity a " +
            "JOIN a.groupAgentEntityList gl " +
            "WHERE d.moment >= :periodStart AND d.moment <= :periodEnd " +
            "AND gl = :groupAgentEntity " +
            "AND dp.products IS NOT NULL " +

            "UNION " +

            "SELECT DISTINCT bc FROM DemandPositionsEntity dp " +
            "LEFT JOIN dp.bundle b " +
            "LEFT JOIN b.bundleProducts bp " +
            "LEFT JOIN bp.product bp_product " +
            "LEFT JOIN bp_product.categoryEntity bc " +
            "JOIN dp.demandEntity d " +
            "JOIN d.agentEntity a " +
            "JOIN a.groupAgentEntityList gl " +
            "WHERE d.moment >= :periodStart AND d.moment <= :periodEnd " +
            "AND gl = :groupAgentEntity " +
            "AND dp.bundle IS NOT NULL")
        List<CategoryEntity> getListUniqueCategoryEntity(@Param("periodStart") LocalDateTime periodStart,
                                                         @Param("periodEnd") LocalDateTime periodEnd,
                                                         @Param("groupAgentEntity") GroupAgentEntity groupAgentEntity);

    @Query("SELECT DISTINCT c FROM DemandPositionsEntity dp " +
            "LEFT JOIN dp.products p " +
            "LEFT JOIN p.categoryEntity c " +
            "JOIN dp.demandEntity d " +
            "JOIN d.agentEntity a " +
            "WHERE d.moment >= :periodStart AND d.moment <= :periodEnd " +
            "AND a = :agentEntity " +
            "AND dp.products IS NOT NULL " +

            "UNION " +

            "SELECT DISTINCT bc FROM DemandPositionsEntity dp " +
            "LEFT JOIN dp.bundle b " +
            "LEFT JOIN b.bundleProducts bp " +
            "LEFT JOIN bp.product bp_product " +
            "LEFT JOIN bp_product.categoryEntity bc " +
            "JOIN dp.demandEntity d " +
            "JOIN d.agentEntity a " +
            "WHERE d.moment >= :periodStart AND d.moment <= :periodEnd " +
            "AND a = :agentEntity " +
            "AND dp.bundle IS NOT NULL")
    List<CategoryEntity> getListUniqueCategoryEntityByAgent(@Param("periodStart") LocalDateTime periodStart,
                                                     @Param("periodEnd") LocalDateTime periodEnd,
                                                     @Param("agentEntity") AgentEntity agentEntity);

}

