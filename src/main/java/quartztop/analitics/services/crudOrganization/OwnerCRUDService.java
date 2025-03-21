package quartztop.analitics.services.crudOrganization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.OwnerDTO;
import quartztop.analitics.models.organizationData.OwnerEntity;
import quartztop.analitics.repositories.organizationData.OwnerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OwnerCRUDService {
    private final OwnerRepository ownerRepository;

    public OwnerEntity create(OwnerDTO ownerDTO) {
        OwnerEntity ownerEntity = mapToEntity(ownerDTO);
        return ownerRepository.save(ownerEntity);
    }

    public Optional<OwnerEntity> getOptionalEntity(OwnerDTO ownerDTO) {
        return ownerRepository.findById(ownerDTO.getId());
    }

    public static OwnerEntity mapToEntity(OwnerDTO owner) {

        OwnerEntity ownerEntity = new OwnerEntity();
        ownerEntity.setId(owner.getId());
        ownerEntity.setName(owner.getName());
        ownerEntity.setEmail(owner.getEmail());
        ownerEntity.setFullName(owner.getFullName());
        ownerEntity.setLastName(owner.getLastName());
        ownerEntity.setMiddleName(owner.getMiddleName());
        ownerEntity.setFirstName(owner.getFirstName());
        ownerEntity.setUid(owner.getUid());
        return ownerEntity;
    }
    public static OwnerDTO mapToDTO(OwnerEntity owner) {

        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setId(owner.getId());
        ownerDTO.setName(owner.getName());
        ownerDTO.setEmail(owner.getEmail());
        ownerDTO.setFullName(owner.getFullName());
        ownerDTO.setLastName(owner.getLastName());
        ownerDTO.setMiddleName(owner.getMiddleName());
        ownerDTO.setFirstName(owner.getFirstName());
        ownerDTO.setUid(owner.getUid());
        return ownerDTO;
    }
}
