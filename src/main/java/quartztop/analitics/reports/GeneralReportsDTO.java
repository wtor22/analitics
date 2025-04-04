package quartztop.analitics.reports;

import lombok.Data;
import quartztop.analitics.dtos.organizationData.OwnerDTO;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class GeneralReportsDTO {

    private List<ReportByGroupAgentDTO> reportByGroupAgentDTOList = new ArrayList<>();

    private LocalDateTime startPeriod;
    private LocalDateTime endPeriod;
    private LocalDateTime startComparePeriod;
    private LocalDateTime endComparePeriod;
    private OwnerDTO ownerDTO;
    private double totalCurrentCount;
    private double totalCompareCount;
}
