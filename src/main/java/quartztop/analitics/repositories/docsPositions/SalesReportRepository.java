package quartztop.analitics.repositories.docsPositions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import quartztop.analitics.models.docsPositions.DemandPositionsEntity;
import quartztop.analitics.reports.salesReportToExcel.RatingProductReportDTO;
import quartztop.analitics.reports.salesReportToExcel.SalesReportDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SalesReportRepository extends JpaRepository<DemandPositionsEntity, UUID> {

    @Query(value = """
            WITH real_positions AS (SELECT
                    d.moment,
                    o.name AS organization_name,
            		c.name AS category_name,
            		p.name AS product_name,
                    dp.product_id,
                    dp.quantity,
                    dp.quantity * dp.price * (1 - COALESCE(dp.discount, 0) / 100.0) AS sum
                FROM
                    demand_positions dp
                    JOIN demands d ON dp.demand_id = d.id
                    JOIN organizations o ON d.organization_id = o.id
            		JOIN products p ON dp.product_id = p.id
            		JOIN product_categories c ON c.id = p.category_id
                WHERE
                    dp.type_product = 'product'
                    AND d.moment BETWEEN :start AND :end
                    AND d.applicable = true
                    AND d.agent_id NOT IN (:excludedAgents)
                UNION ALL
                        
            SELECT
            		d.moment,
            		o.name AS organization_name,
            		c.name AS category_name,
            		p.name AS product_name,
            		bp.product_id,
            		(bp.quantity * dp.quantity) AS quantity,
            		dp.quantity * dp.price * (1 - COALESCE(dp.discount, 0) / 100.0) AS sum
            	FROM
            		demand_positions dp
            		JOIN demands d ON dp.demand_id = d.id
            		JOIN organizations o ON d.organization_id = o.id
            		JOIN bundle_products bp ON dp.bundle_id = bp.bundle_id
            		JOIN products p ON bp.product_id = p.id
            		JOIN product_categories c ON c.id = p.category_id
            	WHERE
            		dp.type_product = 'bundle'
            		AND d.moment BETWEEN :start AND :end
                    AND d.applicable = true
                    AND d.agent_id NOT IN (:excludedAgents)
            )
            SELECT
                DATE_TRUNC('month', rp.moment) AS month,
                rp.organization_name,
            	rp.category_name,
                SUM(rp.quantity) AS total_quantity,
                SUM(rp.sum) AS total_sum
            FROM
                real_positions rp
            GROUP BY
                month, rp.organization_name, rp.category_name
            ORDER BY
                month, rp.organization_name;
            """, nativeQuery = true)
    List<SalesReportDTO> getSalesReport(@Param("start") LocalDateTime startOfYear,
                                        @Param("end") LocalDateTime endOfYear,
                                        @Param("excludedAgents") List<UUID> excludedAgents);


    @Query(value = """
            WITH real_positions AS (SELECT
                    d.moment,
                    o.name AS organization_name,
            		c.name AS category_name,
            		p.name AS product_name,
                    dp.product_id,
                    dp.quantity
                FROM
                    demand_positions dp
                    JOIN demands d ON dp.demand_id = d.id
                    JOIN organizations o ON d.organization_id = o.id
            		JOIN products p ON dp.product_id = p.id
            		JOIN product_categories c ON c.id = p.category_id
                WHERE
                    dp.type_product = 'product'
                    AND d.moment BETWEEN :start AND :end
                    AND d.applicable = true
                    AND d.agent_id NOT IN (:excludedAgents)
                UNION ALL
                        
            SELECT
            		d.moment,
            		o.name AS organization_name,
            		c.name AS category_name,
            		p.name AS product_name,
            		bp.product_id,
            		(bp.quantity * dp.quantity) AS quantity
            	FROM
            		demand_positions dp
            		JOIN demands d ON dp.demand_id = d.id
            		JOIN organizations o ON d.organization_id = o.id
            		JOIN bundle_products bp ON dp.bundle_id = bp.bundle_id
            		JOIN products p ON bp.product_id = p.id
            		JOIN product_categories c ON c.id = p.category_id
            	WHERE
            		dp.type_product = 'bundle'
            		AND d.moment BETWEEN :start AND :end
                    AND d.applicable = true
                    AND d.agent_id NOT IN (:excludedAgents)
            ) ,
            stock_summary AS (
            	SELECT
            		product_id,
            		SUM(stock) AS stock
            	FROM stock_by_store
            	GROUP BY product_id
            )
            SELECT
                rp.product_name,
            	rp.category_name,
                SUM(rp.quantity) AS total_quantity,
                ss.stock AS stock
            FROM
                real_positions rp
            JOIN stock_summary ss ON rp.product_id = ss.product_id
            GROUP BY
                rp.product_name, rp.category_name, ss.stock
            ORDER BY
                rp.category_name, total_quantity DESC;
            """, nativeQuery = true)
    List<RatingProductReportDTO> getRatingProductReport(@Param("start") LocalDateTime startOfYear,
                                                        @Param("end") LocalDateTime endOfYear,
                                                        @Param("excludedAgents") List<UUID> excludedAgents);

}
