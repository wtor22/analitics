package quartztop.analitics.reports;

import lombok.Data;
import quartztop.analitics.dtos.counterparty.GroupAgentDTO;
import quartztop.analitics.dtos.products.CategoryDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ReportByGroupAgentDTO {

    private GroupAgentDTO groupAgentDTO;
    private List<ReportByAgentsDTO> reportByAgentsList = new ArrayList<>();
    private List<CategoryToGroup> categoryToGroupList = new ArrayList<>();
    double countCurrentPeriod;
    double countComparePeriod;
    double sumCurrentPeriod;
    double sumComparePeriod;


    private Map<CategoryDTO, Double[]> mapDataCategory = new HashMap<>();
}
