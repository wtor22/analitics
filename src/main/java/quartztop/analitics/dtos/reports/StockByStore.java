package quartztop.analitics.dtos.reports;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import quartztop.analitics.dtos.Meta;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockByStore {
    private Meta meta;
    private String name;
    private int stock;
    private int reserve;
    private int inTransit;
    private UUID productId;
    private UUID storeId;

    public void extractStoreIdFromMeta() {
        if (meta != null && meta.getHref() != null) {
            this.storeId = extractUUID(meta.getHref(), "store");
        }
    }

    private static UUID extractUUID(String href, String type) {
        try {
            Pattern pattern = Pattern.compile(type + "/([a-f0-9\\-]+)");
            Matcher matcher = pattern.matcher(href);
            if (matcher.find()) {
                return UUID.fromString(matcher.group(1));
            }
        } catch (Exception e) {
            // логировать можно, если надо
        }
        return null;
    }
}
