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

        // Достаем rows -> assortment
        JsonNode rowsNode = rootNode.path("rows");

        if (rowsNode.isArray()) {
            for (JsonNode rowNode : rowsNode) {
                JsonNode assortmentNode = rowNode.get("assortment"); // Достаем assortment
                if (assortmentNode != null && assortmentNode.has("meta")) {
                    String assortmentType = assortmentNode.get("meta").get("type").asText();
                    if ("product".equals(assortmentType)) {
                        ProductDTO productDTO = objectMapper.treeToValue(assortmentNode, ProductDTO.class);
                        productDTOList.add(productDTO);
                    }
                }
            }
        }
        return productDTOList;
    }
}
