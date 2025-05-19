package quartztop.analitics.services.crudOrganization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.OrganizationDTO;
import quartztop.analitics.models.organizationData.Organization;
import quartztop.analitics.repositories.organizationData.OrganizationRepository;
import quartztop.analitics.utils.Region;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationCRUDService {
    private final OrganizationRepository organizationRepository;

    public Organization createOreUpdate(OrganizationDTO organizationDTO) {

        Organization organization = mapToEntity(organizationDTO);
        return organizationRepository.save(organization);
    }

    public Optional<Organization> getOptionalEntity(OrganizationDTO organizationDTO) {
        return organizationRepository.findById(organizationDTO.getId());
    }

    public List<Organization> getAll(){
        return organizationRepository.findAll();
    }


    public Organization getOrgByRegion(Region region) {
        // UUID Кварцтоп
        UUID idQuartzTop = UUID.fromString("75229ba5-1af5-11ed-0a80-00d200186bce");
        // UUID Юрита
        UUID idUrita = UUID.fromString("026e31df-2925-11ed-0a80-0d6400240d43");

        UUID orgID;
        
        if(region == Region.RU) {
            orgID = idQuartzTop;
        } else {
            orgID = idUrita;
        }

        return organizationRepository.findById(orgID).orElseThrow();

    }

    public static Organization mapToEntity(OrganizationDTO organization) {

        Organization organizationEntity = new Organization();
        organizationEntity.setId(organization.getId());
        organizationEntity.setName(organization.getName());

        return organizationEntity;
    }

    public static OrganizationDTO mapToDTO(Organization organization) {

        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(organization.getId());
        organizationDTO.setName(organization.getName());

        return organizationDTO;
    }

}
