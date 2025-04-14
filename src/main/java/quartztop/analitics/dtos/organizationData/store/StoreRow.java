package quartztop.analitics.dtos.organizationData.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import quartztop.analitics.dtos.Meta;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreRow {

    private Meta meta;
    private List<StoreDto> listStore;

    public UUID getStoreId() {
        return extractUUID(meta.getHref(), "store");
    }

    private static UUID extractUUID(String href, String type) {
        try {
            Pattern pattern = Pattern.compile(type + "/([a-f0-9\\-]+)");
            Matcher matcher = pattern.matcher(href);
            if (matcher.find()) {
                return UUID.fromString(matcher.group(1));
            }
        } catch (Exception e) {
        }
        return null;
    }
}
