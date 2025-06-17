package quartztop.analitics.repositories.product;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quartztop.analitics.dtos.products.ProductDTO;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<UUID> findIdsWithUpdatedChanged(List<ProductDTO> dtos) {
        if (dtos.isEmpty()) return Collections.emptyList();

        // Формируем VALUES-блок
        String values = dtos.stream()
                .map(dto -> String.format("('%s'::uuid, '%s'::timestamp)",
                        dto.getId(), dto.getUpdated().toString().replace("T", " ")))
                .collect(Collectors.joining(",\n"));

        String sql = """
            WITH remote_data(id, updated) AS (
                VALUES
                %s
            ),
            changed AS (
                SELECT p.id
                FROM products p
                JOIN remote_data r ON p.id = r.id
                WHERE p.updated IS DISTINCT FROM r.updated
            ),
            missing AS (
                SELECT r.id
                FROM remote_data r
                LEFT JOIN products p ON p.id = r.id
                WHERE p.id IS NULL
            )
            SELECT id FROM changed
            UNION ALL
            SELECT id FROM missing;
        """.formatted(values);

        return jdbcTemplate.query(sql, (rs, rowNum) -> UUID.fromString(rs.getString("id")));
    }
}
