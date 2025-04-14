package quartztop.analitics.services.crudProduct;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.products.ProductAttributeDTO;
import quartztop.analitics.models.products.ProductAttributeEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.repositories.product.ProductAttributeRepository;
import quartztop.analitics.repositories.product.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAttributeService {

    private final ProductRepository productRepository;
    private final ProductAttributeRepository attributeRepository;

    public void addAttributesToProduct(ProductsEntity product, List<ProductAttributeDTO> attributes) {
        for (ProductAttributeDTO attr : attributes) {
            ProductAttributeEntity entity = new ProductAttributeEntity();
            entity.setName(attr.getName());
            entity.setValueByType(attr.getType(), attr.getValue());
            entity.setProduct(product); // важно!

            product.getAttributes().add(entity); // связываем с продуктом
        }
    }

    public void updateAttributes(ProductsEntity product, List<ProductAttributeDTO> newAttributes) {
        product.getAttributes().clear(); // удалим старые
        addAttributesToProduct(product, newAttributes); // добавим новые
    }

    private ProductAttributeEntity mapToEntity(ProductAttributeDTO productAttribute) {

        ProductAttributeEntity productAttributeEntity = new ProductAttributeEntity();
        productAttributeEntity.setType(productAttribute.getType());
        productAttributeEntity.setName(productAttribute.getName());
        productAttributeEntity.setValueByType(productAttribute.getValue(),productAttribute.getType());

        return productAttributeEntity;
    }
}

