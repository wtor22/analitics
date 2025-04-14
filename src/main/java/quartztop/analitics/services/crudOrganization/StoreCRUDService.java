package quartztop.analitics.services.crudOrganization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.store.StoreDto;
import quartztop.analitics.models.organizationData.StoreEntity;
import quartztop.analitics.repositories.organizationData.StoreRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<StoreEntity> getListEntity() {
        return storeRepository.findAll();
    }



    public Optional<StoreEntity> getOptionalEntityById(UUID id) {
        return storeRepository.findById(id);
    }

    public static StoreEntity mapToEntity(StoreDto store) {

        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setId(store.getId());
        storeEntity.setName(store.getName());
        storeEntity.setDescription(store.getDescription());
        storeEntity.setExistsInStockReport(store.isExistsInStockReport());
        storeEntity.setNameToBot(store.getNameToBot());

        return storeEntity;
    }
    public static StoreDto mapToDto(StoreEntity store) {

        StoreDto storeDto = new StoreDto();
        storeDto.setId(store.getId());
        storeDto.setName(store.getName());
        storeDto.setDescription(store.getDescription());
        storeDto.setExistsInStockReport(store.isExistsInStockReport());
        storeDto.setNameToBot(store.getNameToBot());

        return storeDto;
    }
}
