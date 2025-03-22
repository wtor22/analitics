package quartztop.analitics.dtos.docs;

import lombok.Data;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.dtos.counterparty.ContractDTO;
import quartztop.analitics.dtos.docsPositions.DemandPositionsDTO;
import quartztop.analitics.dtos.organizationData.OrganizationDTO;
import quartztop.analitics.dtos.organizationData.OwnerDTO;
import quartztop.analitics.dtos.organizationData.StoreDto;
import quartztop.analitics.models.docs.InvoiceOutEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DemandDTO {

    private UUID id; //  внешний id из API
    private boolean applicable;
    private LocalDateTime created; // Момент создания
    private LocalDateTime updated; // Момент последнего обновления
    private LocalDateTime deleted; // Момент последнего удаления отгрузки
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
    private List<DemandPositionsDTO> demandPositionsDTOList;
    private List<InvoiceOutEntity> invoiceOutEntityList;

}
