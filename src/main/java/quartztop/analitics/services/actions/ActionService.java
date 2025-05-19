package quartztop.analitics.services.actions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.actions.ActionDTO;
import quartztop.analitics.models.organizationData.Organization;
import quartztop.analitics.repositories.organizationData.OrganizationRepository;
import quartztop.analitics.responses.actions.TelegramActionDTO;
import quartztop.analitics.exceptions.EntityAlreadyExistsException;
import quartztop.analitics.models.actions.ActionEntity;
import quartztop.analitics.repositories.actions.ActionRepository;
import quartztop.analitics.services.crudOrganization.OrganizationCRUDService;
import quartztop.analitics.utils.Region;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class ActionService {
    private final ActionRepository actionRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationCRUDService organizationCRUDService;

    public ActionDTO saveOrUpdate(ActionDTO actionDTO) {

        ActionEntity actionEntity;
        if (actionDTO.getId() != null) {
            actionEntity = actionRepository.findById(actionDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Not found"));
            actionEntity.setDescription(actionDTO.getDescription());
            actionEntity.setName(actionDTO.getName());
            actionEntity.setContent(actionDTO.getContent());
            actionEntity.setStartActionDate(actionDTO.getStartActionDate());
            actionEntity.setEndActionDate(actionDTO.getEndActionDate());
            actionEntity.setTitleImageUrl(actionDTO.getTitleImageUrl());
        } else {
            actionEntity = mapToEntity(actionDTO);
        }
        // Проверка на уникальность имени, но исключая текущую запись (если обновляем)
        actionRepository.findByName(actionDTO.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(actionDTO.getId())) {
                throw new EntityAlreadyExistsException("Action", actionDTO.getName());
            }
        });

        // Загружаем организации по списку ID
        List<Organization> organizations = organizationRepository.findAllById(actionDTO.getOrganizationIds());
        actionEntity.setActionOrganizationList(organizations);

        ActionEntity saved = actionRepository.save(actionEntity);
        if (actionDTO.getId() != null) {
            log.info("Action \"{}\" was updated", saved.getName());
        } else {
            log.info("Action \"{}\" was created", saved.getName());
        }
        ActionDTO savedActionDto = mapToDto(saved);
        savedActionDto.setOrganizationIds(actionDTO.getOrganizationIds());
        return savedActionDto;
    }

    public List<ActionDTO> getAllDto() {
        List<ActionEntity> actionEntityList = actionRepository.findAll();
        List<ActionDTO> actionDTOS = new ArrayList<>();
        for(ActionEntity action: actionEntityList) {
            ActionDTO actionDTO = mapToDto(action);
            List<UUID> orgIds = action.
                    getActionOrganizationList()
                    .stream()
                    .map(Organization::getId)
                    .toList();
            actionDTO.setOrganizationIds(orgIds);
            actionDTOS.add(actionDTO);
        }
        return actionDTOS;
    }
    public List<ActionDTO> getAllDtoByRegion() {
        List<ActionEntity> actionEntityList = actionRepository.findAll();
        List<ActionDTO> actionDTOS = new ArrayList<>();
        for(ActionEntity action: actionEntityList) {
            ActionDTO actionDTO = mapToDto(action);
            List<UUID> orgIds = action.
                    getActionOrganizationList()
                    .stream()
                    .map(Organization::getId)
                    .toList();
            actionDTO.setOrganizationIds(orgIds);
            actionDTOS.add(actionDTO);
        }
        return actionDTOS;
    }

    public ActionDTO getDtoById(long id) {

        ActionEntity actionEntity = actionRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Not found action with id " + id)
        );
        ActionDTO actionDTO = mapToDto(actionEntity);
        List<UUID> orgIds = actionEntity.
                getActionOrganizationList()
                .stream()
                .map(Organization::getId)
                .toList();
        actionDTO.setOrganizationIds(orgIds);
        return actionDTO;
    }

    public List<TelegramActionDTO> getTelegramDTO() {
        List<ActionEntity> actionEntityList = actionRepository.findAll();
        List<TelegramActionDTO> list = new ArrayList<>();
        for(ActionEntity action : actionEntityList) {

            TelegramActionDTO telegramActionDTO = TelegramActionDTO.builder()
                    .name(action.getName())
                    .description(action.getDescription())
                    .titleImageUrl(action.getTitleImageUrl())
                    .id(action.getId())
                    .build();
            list.add(telegramActionDTO);
        }
        return list;
    }

    public static ActionDTO mapToDto(ActionEntity action) {

        ActionDTO actionDTO = new ActionDTO();
        actionDTO.setId(action.getId());
        actionDTO.setName(action.getName());
        actionDTO.setDescription(action.getDescription());
        actionDTO.setContent(action.getContent());
        actionDTO.setTitleImageUrl(action.getTitleImageUrl());
        actionDTO.setStartActionDate(action.getStartActionDate());
        actionDTO.setEndActionDate(action.getEndActionDate());
        actionDTO.setActive(action.isActive());

        return actionDTO;
    }

    public static ActionEntity mapToEntity(ActionDTO action) {

        ActionEntity actionEntity = new ActionEntity();
        actionEntity.setId(action.getId());
        actionEntity.setName(action.getName());
        actionEntity.setDescription(action.getDescription());
        actionEntity.setContent(action.getContent());
        actionEntity.setTitleImageUrl(action.getTitleImageUrl());
        actionEntity.setStartActionDate(action.getStartActionDate());
        actionEntity.setEndActionDate(action.getEndActionDate());
        actionEntity.setActive(action.isActive());

        return actionEntity;
    }

    public Optional<TelegramActionDTO> getNextAction(Long currentId, Region region) {

        Organization organization = organizationCRUDService.getOrgByRegion(region);

        PageRequest pageRequest = PageRequest.of(0, 1);
        List<ActionEntity> result = actionRepository.findNextAction(currentId, LocalDate.now(), organization, pageRequest);
        Optional<ActionEntity> optionalEntity = result.stream().findFirst();


        return optionalEntity.map(entity -> TelegramActionDTO.builder()
                .name(entity.getName())
                .titleImageUrl(entity.getTitleImageUrl())
                .description(entity.getDescription())
                .id(entity.getId())
                .build());
    }
}
