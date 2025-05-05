package quartztop.analitics.integration.botApiResponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.dtos.actions.ActionDTO;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ActionStatistics {

    private Long actionId;
    private ActionDTO actionDTO;
    private int countMoreDetailsClick;
}
