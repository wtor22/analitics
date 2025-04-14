package quartztop.analitics.models.products;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "product_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    @Column(name = "value_string")
    private String valueString;

    @Column(name = "value_long")
    private Long valueLong;

    @Column(name = "value_link")
    private String valueLink;

    // связь с товаром
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductsEntity product;

    // Метод, который будет принимать тип и строковое значение и сохранять его в нужное поле
    public void setValueByType(String type, String value) {
        this.type = type;
        switch (type) {
            case "string":
                this.valueString = value;
                break;
            case "long":
                this.valueLong = Long.parseLong(value);
                break;
            case "link":
                this.valueLink = value;
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип атрибута: " + type);
        }
    }

    // Метод для получения значения в строковом виде (например, для вывода в UI)
    public String getDisplayValue() {
        return switch (type) {
            case "string" -> valueString;
            case "long" -> String.valueOf(valueLong);
            case "link" -> valueLink;
            default -> null;
        };
    }
}

