package quartztop.analitics.services.crudOrganization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.store.StoreDto;
import quartztop.analitics.models.organizationData.StoreEntity;
import quartztop.analitics.repositories.organizationData.StoreRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreCRUDService {
    private final StoreRepository storeRepository;

    public StoreEntity create(StoreDto storeDto) {
        return storeRepository.save(mapToEntity(storeDto));
    }
    public Optional<StoreEntity> getOptionalEntity(StoreDto storeDto) {
        return storeRepository.findById(storeDto.getId());
    }

    public void updateAlias(UUID id, String alias) {
        Optional<StoreEntity> optionalStoreEntity = storeRepository.findById(id);
        if(optionalStoreEntity.isEmpty()) return;
        StoreEntity storeEntity = optionalStoreEntity.get();
        storeEntity.setNameToBot(alias);
        storeRepository.save(storeEntity);
    }

    public void updateOrder(UUID id, String alias) {
        Optional<StoreEntity> optionalStoreEntity = storeRepository.findById(id);
        if(optionalStoreEntity.isEmpty()) return;
        storeRepository.save(optionalStoreEntity.get()).setNameToBot(alias);
    }

    public  void setIndexOrderInReport(List<UUID> storeUUIDList) {
        List<StoreEntity> allListStores = storeRepository.findAll();
        for(StoreEntity store : allListStores) {
            if(storeUUIDList.contains(store.getId())) {
                int index = storeUUIDList.indexOf(store.getId()) + 1;
                store.setOrderInBotIndex(index);
            } else {
                store.setOrderInBotIndex(0);
            }
        }
        storeRepository.saveAll(allListStores);
    }




    public List<StoreEntity> getListEntity() {
        return storeRepository.findAll();
    }

    public List<StoreDto> getListDtoExistingInStockReport() {
        List<StoreEntity> storeEntityList = storeRepository.findAllOrdered();
        return storeEntityList.stream().map(StoreCRUDService::mapToDto).toList();
    }
    public List<StoreDto> getListDtoNotExistingInStockReport() {
        List<StoreEntity> storeEntityList = storeRepository.findAllNotOrdered();
        return storeEntityList.stream().map(StoreCRUDService::mapToDto).toList();
    }


    public Optional<StoreEntity> getOptionalEntityById(UUID id) {
        return storeRepository.findById(id);
    }

    public static StoreEntity mapToEntity(StoreDto store) {

        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setId(store.getId());
        storeEntity.setName(store.getName());
        storeEntity.setDescription(store.getDescription());
        storeEntity.setOrderInBotIndex(store.getOrderInBotIndex());
        storeEntity.setNameToBot(store.getNameToBot());

        return storeEntity;
    }
    public static StoreDto mapToDto(StoreEntity store) {

        StoreDto storeDto = new StoreDto();
        storeDto.setId(store.getId());
        storeDto.setName(store.getName());
        storeDto.setDescription(store.getDescription());
        storeDto.setOrderInBotIndex(store.getOrderInBotIndex());
        storeDto.setNameToBot(store.getNameToBot());

        return storeDto;
    }
}
