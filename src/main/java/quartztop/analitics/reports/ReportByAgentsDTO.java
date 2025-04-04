package quartztop.analitics.reports;

import lombok.Data;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.reports.ReportsByCategoryDTO;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReportByAgentsDTO {
    private AgentDTO agentDTO;
    private List<ReportsByCategoryDTO> reportsByCategoryList = new ArrayList<>();
    double countCurrentPeriod;
    double countComparePeriod;
    double sumCurrentPeriod;
    double sumComparePeriod;
}
