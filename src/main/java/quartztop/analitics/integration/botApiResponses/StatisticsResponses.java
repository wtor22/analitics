package quartztop.analitics.integration.botApiResponses;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponses {
    private long usersCount;
    private long usersNotActiveStatusCount;
    private long clickTabNextActionCount;
    private long clickTabActions;
    private List<ActionStatistics> actionStatisticsList;
}
