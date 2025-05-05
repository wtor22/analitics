package quartztop.analitics.dtos.actions;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ActionDTO {

    private Long id;
    private String name;
    private String content;
    private String description;
    private String titleImageUrl;
    private LocalDate startActionDate;
    private LocalDate endActionDate;
}
