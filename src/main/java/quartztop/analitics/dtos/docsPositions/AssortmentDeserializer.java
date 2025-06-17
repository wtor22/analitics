package quartztop.analitics.dtos.docsPositions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssortmentDeserializer extends JsonDeserializer<Object> {
    private final ObjectMapper objectMapper;

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String type = node.get("meta").get("type").asText();

        log.info("PRINT TYPE " + type);

        // Игнорируем поле meta, работаем только с нужными полями
        JsonNode metaNode = node.get("meta");
        if (metaNode != null) {
            ((ObjectNode) node).remove("meta");
        }

        // Проверяем тип: если "bundle", то возвращаем BundleDTO

        if ("bundle".equals(type)) {
            // Де сериализация BundleDTO
            BundleDTO bundleDTO = objectMapper.treeToValue(node, BundleDTO.class);

            // Если есть поле "components", десериализуем его как список ProductDTO
            JsonNode componentsNode = node.get("components").get("rows");
            if (componentsNode != null && componentsNode.isArray()) {
                List<ProductDTO> productDTOList = new ArrayList<>();
                for (JsonNode componentNode : componentsNode) {
                    ProductDTO productDTO = objectMapper.treeToValue(componentNode, ProductDTO.class);
                    log.info("ProductDTO id " + productDTO.getId());
                    productDTOList.add(productDTO);
                }
                productDTOList.forEach(s -> log.info(s.getArticle()));
                bundleDTO.setProductDTOList(productDTOList); // Предполагается, что в BundleDTO есть поле для списка продуктов
            }

            return bundleDTO;
        }

        // Если "product", то возвращаем ProductDTO
        else if ("product".equals(type)) {
            return objectMapper.treeToValue(node, ProductDTO.class);
        }

        return null; // Если тип неизвестен
    }
}

