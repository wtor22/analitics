package quartztop.analitics.models.docs;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.ContractEntity;
import quartztop.analitics.models.docsPositions.DemandPositionsEntity;
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
@Table(name = "demands")
public class DemandEntity {

    @Id
    private UUID id; //  внешний id из API

    private boolean applicable;
    private LocalDateTime created; // Момент создания
    private LocalDateTime updated; // Момент последнего обновления
    private LocalDateTime deleted; // Момент последнего удаления отгрузки
    private LocalDateTime moment; // Момент отгрузки
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

    @ManyToMany
    @JoinTable(
            name = "demands_invoices", // Название промежуточной таблицы
            joinColumns = @JoinColumn(name = "demand_id"),  // Связь с таблицей комплектов
            inverseJoinColumns = @JoinColumn(name = "invoice_id") // Связь с таблицей товаров
    )
    private List<InvoiceOutEntity> invoiceOutEntityList = new ArrayList<>();

    // Удаляю связанные позиции при удалении отгрузки
    @OneToMany(mappedBy = "demandEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DemandPositionsEntity> demandPositionsEntityList = new ArrayList<>();

    public void addPosition(DemandPositionsEntity position) {
        if (!demandPositionsEntityList.contains(position)) {
            demandPositionsEntityList.add(position);
            position.setDemandEntity(this);
        }
    }
}
