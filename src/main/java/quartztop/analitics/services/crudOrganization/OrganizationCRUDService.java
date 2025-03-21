package quartztop.analitics.services.crudOrganization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.OrganizationDTO;
import quartztop.analitics.models.organizationData.Organization;
import quartztop.analitics.repositories.organizationData.OrganizationRepository;

import java.util.Optional;

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
