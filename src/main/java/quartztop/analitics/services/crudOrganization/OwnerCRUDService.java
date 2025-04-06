package quartztop.analitics.services.crudOrganization;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.OwnerDTO;
import quartztop.analitics.models.organizationData.OwnerEntity;
import quartztop.analitics.repositories.organizationData.OwnerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerCRUDService {
    private final OwnerRepository ownerRepository;

    public OwnerEntity create(OwnerDTO ownerDTO) {
        OwnerEntity ownerEntity = mapToEntity(ownerDTO);
        return ownerRepository.save(ownerEntity);
    }
    public List<OwnerDTO> getListOwnersDTO() {
        List<OwnerEntity> ownerEntityList = ownerRepository.findAll(Sort.by(Sort.Direction.ASC, "fullName"));

        return ownerEntityList.stream().map(OwnerCRUDService::mapToDTO).toList();
    }
    public List<OwnerDTO> getListOwnerByUsedInReportTrue(){
        List<OwnerEntity> ownerEntityList =  ownerRepository.findAllByUsedInReportsTrue();
        return ownerEntityList.stream().map(OwnerCRUDService::mapToDTO).toList();
    }

    public List<OwnerDTO> setListOwnerUsedInReports(List<UUID> listOwnersUUID) {
        List<OwnerEntity> ownerEntityList = ownerRepository.findAll();
        ownerEntityList.forEach(o -> {
            if(listOwnersUUID.contains(o.getId())) {
                o.setUsedInReports(true);
            } else {
                o.setUsedInReports(false);
            }
        });
        List<OwnerEntity> updatedListOwners = ownerRepository.saveAll(ownerEntityList);
        return updatedListOwners.stream().map(OwnerCRUDService::mapToDTO).toList();
    }

    public Optional<OwnerEntity> getOptionalEntity(OwnerDTO ownerDTO) {
        return ownerRepository.findById(ownerDTO.getId());
    }
    public OwnerDTO getOwnerDto(UUID id) {
        Optional<OwnerEntity> optionalOwnerEntity = ownerRepository.findById(id);
        return optionalOwnerEntity.map(OwnerCRUDService::mapToDTO).orElse(null);
    }

    public Optional<OwnerEntity> getOptionalEntity(UUID id) {
        return ownerRepository.findById(id);
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
        ownerEntity.setUsedInReports(owner.isUsedInReports());
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
        ownerDTO.setUsedInReports(owner.isUsedInReports());
        return ownerDTO;
    }
}
