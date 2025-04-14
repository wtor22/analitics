package quartztop.analitics.dtos.docs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.dtos.counterparty.ContractDTO;
import quartztop.analitics.dtos.docsPositions.DemandPositionsDTO;
import quartztop.analitics.dtos.organizationData.OrganizationDTO;
import quartztop.analitics.dtos.organizationData.OwnerDTO;
import quartztop.analitics.dtos.organizationData.store.StoreDto;
import quartztop.analitics.models.docs.InvoiceOutEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DemandDTO {

    private final String wordRequest = "demand";
    private UUID id; //  внешний id из API
    private boolean applicable;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime created; // Момент создания
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updated; // Момент последнего обновления
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime deleted; // Момент последнего удаления отгрузки
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime moment; // Момент отгрузки
    private String description; // Комментарий к отгрузке
    private String name;
    private float payedSum; // Сумма оплат по отгрузке
    private int sum; // Сумма отгрузки в копейках

    private OrganizationDTO organization;
    private StoreDto store;
    private OwnerDTO owner;
    private AgentDTO agent;
    private ContractDTO contract;

//    @JsonIgnore
//    private Object positions; // Игнорируем верхний уровень объекта
//    private List<DemandPositionsDTO> rows; // Jackson автоматически парсит массив из "rows"

    private PositionsWrapper positions; // Объект, а не List

    @Data
    public static class PositionsWrapper {
        private List<DemandPositionsDTO> rows; // Теперь Jackson корректно достанет массив
    }

    private List<InvoiceOutEntity> invoiceOutEntityList;

}
