package quartztop.analitics.dtos.actions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionStatusDTO {
    private Long id;
    private boolean active;
}
