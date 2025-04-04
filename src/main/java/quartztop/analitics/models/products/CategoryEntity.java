package quartztop.analitics.models.products;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "product_categories")
public class CategoryEntity {

    @Id
    private UUID id;
    private String code;
    private String description;
    private String name;
    private String pathName;
}
