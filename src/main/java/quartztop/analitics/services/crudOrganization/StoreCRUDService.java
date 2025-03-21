package quartztop.analitics.services.crudOrganization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.StoreDto;
import quartztop.analitics.models.organizationData.StoreEntity;
import quartztop.analitics.repositories.organizationData.StoreRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreCRUDService {
    private final StoreRepository storeRepository;

    public StoreEntity create(StoreDto storeDto) {
        return storeRepository.save(mapToEntity(storeDto));
    }
    public Optional<StoreEntity> getOptionalEntity(StoreDto storeDto) {
        return storeRepository.findById(storeDto.getId());
    }

    public static StoreEntity mapToEntity(StoreDto store) {

        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setId(store.getId());
        storeEntity.setName(store.getName());

        return storeEntity;
    }
    public static StoreDto mapToDto(StoreEntity store) {

        StoreDto storeDto = new StoreDto();
        storeDto.setId(store.getId());
        storeDto.setName(store.getName());

        return storeDto;
    }
}
