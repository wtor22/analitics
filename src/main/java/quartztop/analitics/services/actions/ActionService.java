package quartztop.analitics.services.actions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.actions.ActionDTO;
import quartztop.analitics.dtos.actions.TelegramActionDTO;
import quartztop.analitics.exceptions.EntityAlreadyExistsException;
import quartztop.analitics.models.actions.ActionEntity;
import quartztop.analitics.repositories.actions.ActionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ActionService {
    private final ActionRepository actionRepository;

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

        ActionEntity saved = actionRepository.save(actionEntity);
        if (actionDTO.getId() != null) {
            log.info("Action \"{}\" was updated", saved.getName());
        } else {
            log.info("Action \"{}\" was created", saved.getName());
        }
        log.error("SAVE ACTION " + saved.getId());
        return mapToDto(saved);
    }

    public List<ActionDTO> getAllDto() {
        List<ActionEntity> actionEntityList = actionRepository.findAll();
        return actionEntityList.stream().map(ActionService::mapToDto).toList();
    }

    public ActionDTO getDtoById(long id) {

        ActionEntity actionEntity = actionRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Not found action with id " + id)
        );
        return mapToDto(actionEntity);
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

        return actionEntity;
    }

    public Optional<TelegramActionDTO> getNextAction(Long currentId) {
        Optional<ActionEntity> optionalEntity = (currentId == null)
                ? actionRepository.findFirstByOrderByIdAsc()
                : actionRepository.findFirstByIdGreaterThanOrderByIdAsc(currentId);

        return optionalEntity.map(entity -> TelegramActionDTO.builder()
                .name(entity.getName())
                .titleImageUrl(entity.getTitleImageUrl())
                .description(entity.getDescription())
                .id(entity.getId())
                .build());
    }
}
