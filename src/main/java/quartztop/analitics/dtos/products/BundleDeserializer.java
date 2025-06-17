package quartztop.analitics.dtos.products;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BundleDeserializer extends JsonDeserializer<Object> {
    private final ObjectMapper objectMapper;
    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // Игнорируем поле meta
        JsonNode metaNode = node.get("meta");
        if (metaNode != null) {
            ((ObjectNode) node).remove("meta");
        }
        return objectMapper.treeToValue(node, BundleDTO.class);
    }
}
