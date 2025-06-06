package quartztop.analitics.dtos.products;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductListDeserializer extends JsonDeserializer<List<ProductDTO>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<ProductDTO> deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);
        List<ProductDTO> productDTOList = new ArrayList<>();

        JsonNode rowsNode = rootNode.path("rows");

        if (rowsNode.isArray()) {
            for (JsonNode rowNode : rowsNode) {
                JsonNode assortmentNode = rowNode.get("assortment"); // Достаем assortment
                if (assortmentNode != null && assortmentNode.has("meta")) {
                    String assortmentType = assortmentNode.get("meta").get("type").asText();
                    if ("product".equals(assortmentType)) {
                        ProductDTO productDTO = objectMapper.treeToValue(assortmentNode, ProductDTO.class);

                        // Достаем кастомные атрибуты (если они приходят)
                        JsonNode attributesNode = assortmentNode.path("attributes");
                        if (attributesNode.isArray()) {
                            for (JsonNode attrNode : attributesNode) {
                                String name = attrNode.path("name").asText();
                                String type = attrNode.path("type").asText();
                                String value = attrNode.path("value").asText();

                                ProductAttributeDTO attrDTO = new ProductAttributeDTO();
                                attrDTO.setName(name);
                                attrDTO.setType(type);
                                attrDTO.setValue(value);

                                productDTO.getAttributes().add(attrDTO);
                            }
                        }



                        // Достаем quantity из rowNode
                        double quantity = rowNode.has("quantity") ? rowNode.get("quantity").asDouble() : 0;
                        productDTO.setQuantity(quantity);

                        productDTOList.add(productDTO);

                    }
                }
            }
        }
        return productDTOList;
    }
}
