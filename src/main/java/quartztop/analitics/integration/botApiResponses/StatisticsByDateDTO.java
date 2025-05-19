package quartztop.analitics.integration.botApiResponses;

import lombok.Getter;

@Getter
public class StatisticsByDateDTO {

    private long countRegistration;
    private long countClickAction;
    private long countClickNextAction;
    private long countClickPhoto;
    private long countSearchRequest;
    private long countCreateQuestions;
}
