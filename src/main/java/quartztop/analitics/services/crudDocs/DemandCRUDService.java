package quartztop.analitics.services.crudDocs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.dtos.counterparty.ContractDTO;
import quartztop.analitics.dtos.docs.DemandDTO;
import quartztop.analitics.dtos.organizationData.OrganizationDTO;
import quartztop.analitics.dtos.organizationData.OwnerDTO;
import quartztop.analitics.dtos.organizationData.StoreDto;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.ContractEntity;
import quartztop.analitics.models.docs.DemandEntity;
import quartztop.analitics.models.organizationData.Organization;
import quartztop.analitics.models.organizationData.OwnerEntity;
import quartztop.analitics.models.organizationData.StoreEntity;
import quartztop.analitics.repositories.docs.DemandRepository;
import quartztop.analitics.services.counterparty.AgentCRUDService;
import quartztop.analitics.services.counterparty.ContractCRUDService;
import quartztop.analitics.services.crudOrganization.OrganizationCRUDService;
import quartztop.analitics.services.crudOrganization.OwnerCRUDService;
import quartztop.analitics.services.crudOrganization.StoreCRUDService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandCRUDService {
    private final DemandRepository demandRepository;
    private final OrganizationCRUDService organizationCRUDService;
    private final StoreCRUDService storeCRUDService;
    private final AgentCRUDService agentCRUDService;
    private final OwnerCRUDService ownerCRUDService;
    private final ContractCRUDService contractCRUDService;


    @Transactional
    public void create(DemandDTO demandDTO) {

        DemandEntity demandEntity = mapToEntity(demandDTO);

        setOrganization(demandEntity, demandDTO.getOrganization());
        setStore(demandEntity, demandDTO.getStore());
        setAgent(demandEntity,demandDTO.getAgent());
        setOwner(demandEntity,demandDTO.getOwner());
        setContract(demandEntity,demandDTO.getContract());
        demandRepository.save(demandEntity);
    }

    private void setOrganization(DemandEntity demandEntity, OrganizationDTO organizationDTO) {
        Optional<Organization> optionalOrganization = organizationCRUDService.getOptionalEntity(organizationDTO);
        if(optionalOrganization.isEmpty()) {
            log.info("Organization NOT FOUND");
            Organization organization = organizationCRUDService.createOreUpdate(organizationDTO);
            demandEntity.setOrganization(organization);
            log.info("Organisation {} was created", organization.getName());
        } else {
            demandEntity.setOrganization(optionalOrganization.get());
        }
    }

    public void setStore(DemandEntity demandEntity, StoreDto storeDto) {

        Optional<StoreEntity> optionalStoreEntity = storeCRUDService.getOptionalEntity(storeDto);
        if (optionalStoreEntity.isEmpty()) {
            log.info("Store {} NOT FOUND", storeDto.getName());
            StoreEntity storeEntity = storeCRUDService.create(storeDto);
            demandEntity.setStoreEntity(storeEntity);
            log.info("Store {} was created", storeEntity.getName());
        } else demandEntity.setStoreEntity(optionalStoreEntity.get());
    }

    public void setAgent(DemandEntity demandEntity, AgentDTO agentDTO) {
        Optional<AgentEntity> optionalAgentEntity = agentCRUDService.getOptionalEntity(agentDTO);
        if (optionalAgentEntity.isEmpty()) {
            log.info("Agent {} NOT FOUND", agentDTO.getName());
            AgentEntity agentEntity = agentCRUDService.create(agentDTO);
            demandEntity.setAgentEntity(agentEntity);
            log.info("Agent {} wos created" , agentEntity.getName());
        } else {
            demandEntity.setAgentEntity(optionalAgentEntity.get());
        }
    }

    public void setOwner(DemandEntity demandEntity, OwnerDTO ownerDTO) {
        Optional<OwnerEntity> optionalOwnerEntity = ownerCRUDService.getOptionalEntity(ownerDTO);
        if (optionalOwnerEntity.isEmpty()) {
            log.info("Owner {} NOT FOUND", ownerDTO.getUid());
            OwnerEntity ownerEntity = ownerCRUDService.create(ownerDTO);
            log.info("Owner {} was created", ownerEntity.getUid());
        } else {
            demandEntity.setOwnerEntity(optionalOwnerEntity.get());
        }
    }

    public void setContract(DemandEntity demandEntity, ContractDTO contractDTO) {

        Optional<ContractEntity> optionalContractDTO = contractCRUDService.getOptionalEntity(contractDTO);
        if (optionalContractDTO.isEmpty()) {
            log.info("Contract {} FROM {} NOT FOUND", contractDTO.getName(), contractDTO.getMoment());
            ContractEntity contract = contractCRUDService.create(contractDTO);
            log.info("Contract {} FROM {} was created", contract.getName(), contract.getMoment());
        }

    }

    public static DemandDTO mapToDTO(DemandEntity demand) {

        DemandDTO demandDTO = new DemandDTO();
        demandDTO.setId(demand.getId());
        demandDTO.setDeleted(demand.getDeleted());
        demandDTO.setCreated(demand.getCreated());
        demandDTO.setUpdated(demand.getUpdated());
        demandDTO.setMoment(demand.getMoment());
        demandDTO.setName(demand.getName());
        demandDTO.setDescription(demand.getDescription());
        demandDTO.setApplicable(demand.isApplicable());
        demandDTO.setSum(demand.getSum());

        return demandDTO;
    }

    public static DemandEntity mapToEntity(DemandDTO demand) {

        DemandEntity demandEntity = new DemandEntity();
        demandEntity.setId(demand.getId());
        demandEntity.setDeleted(demand.getDeleted());
        demandEntity.setCreated(demand.getCreated());
        demandEntity.setUpdated(demand.getUpdated());
        demandEntity.setMoment(demand.getMoment());
        demandEntity.setName(demand.getName());
        demandEntity.setDescription(demand.getDescription());
        demandEntity.setApplicable(demand.isApplicable());
        demandEntity.setSum(demand.getSum());

        return demandEntity;
    }
}
