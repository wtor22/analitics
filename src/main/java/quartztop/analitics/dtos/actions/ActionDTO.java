package quartztop.analitics.dtos.actions;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ActionDTO {

    private Long id;
    private String name;
    private String content;
    private String description;
    private String titleImageUrl;
    private LocalDate startActionDate;
    private LocalDate endActionDate;
    private boolean isActive;
    private List<UUID> organizationIds;
}
