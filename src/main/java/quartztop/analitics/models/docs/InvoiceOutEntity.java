package quartztop.analitics.models.docs;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.ContractEntity;
import quartztop.analitics.models.docsPositions.InvoiceOutPositions;
import quartztop.analitics.models.organizationData.Organization;
import quartztop.analitics.models.organizationData.OwnerEntity;
import quartztop.analitics.models.organizationData.StoreEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "invoices")
public class InvoiceOutEntity {

    @Id
    private UUID id; //  внешний id из API

    private boolean applicable;
    private LocalDateTime created; // Момент создания
    private LocalDateTime updated; // Момент последнего обновления
    private LocalDateTime deleted; // Момент последнего удаления
    private LocalDateTime moment; // Момент счета
    @Column(columnDefinition = "TEXT")
    private String description; // Комментарий к отгрузке
    private String name;
    private float payedSum; // Сумма оплат по отгрузке
    private int sum; // Сумма отгрузки в копейках

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreEntity storeEntity;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private OwnerEntity ownerEntity;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private AgentEntity agentEntity;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private ContractEntity contractEntity;

    // Удаляю связанные позиции при удалении счёта
    @OneToMany(mappedBy = "invoiceOutEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvoiceOutPositions> invoiceOutPositionsList = new ArrayList<>();

    @ManyToMany(mappedBy = "invoiceOutEntityList") // Указываем, что связь уже описана в `Demands`
    private List<DemandEntity> demandEntityList = new ArrayList<>();

}
