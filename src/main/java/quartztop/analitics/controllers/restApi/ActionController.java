package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import quartztop.analitics.dtos.actions.ActionDTO;
import quartztop.analitics.models.actions.ActionEntity;
import quartztop.analitics.repositories.actions.ActionRepository;
import quartztop.analitics.services.FileStorageService;
import quartztop.analitics.services.actions.ActionService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/client/actions")
@RequiredArgsConstructor
@Slf4j
public class ActionController {

    private final FileStorageService fileStorageService;
    private final ActionService actionService;
    private final ActionRepository actionRepository;

    @GetMapping
    public ResponseEntity<List<ActionDTO>> actions(){
        return ResponseEntity.ok(actionService.getAllDto());
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createAction(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String content,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startActionDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endActionDate
    ) {

        String imageUrl = null;
        try {
            if(!imageFile.isEmpty()) {
                imageUrl = fileStorageService.saveImage(imageFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        }

        // Собираем DTO
        ActionDTO actionDTO = new ActionDTO();
        actionDTO.setName(name);
        actionDTO.setDescription(description);
        actionDTO.setContent(content);
        actionDTO.setStartActionDate(startActionDate);
        actionDTO.setEndActionDate(endActionDate);
        actionDTO.setTitleImageUrl(imageUrl);

        actionService.saveOrUpdate(actionDTO);

        System.out.println("Имя акции: " + name);
        System.out.println("Описание: " + description);

        return ResponseEntity.ok("Готово!");
    }

    @PutMapping
    public ResponseEntity<ActionDTO> updateAction(
                                                @RequestParam Long id,
                                                @RequestParam String name,
                                                @RequestParam String description,
                                                @RequestParam String content,
                                                @RequestParam("imageFile") MultipartFile imageFile,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startActionDate,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endActionDate) {

        // 1. Получаем существующую акцию
        ActionEntity existing = actionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Акция не найдена"));

        String oldImageUrl = existing.getTitleImageUrl();
        String newImageUrl = oldImageUrl;
        log.error("PRINT newImageUrl " + newImageUrl);
        try {
            if (!imageFile.isEmpty()) {
                newImageUrl = fileStorageService.saveImage(imageFile);
                if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                    fileStorageService.deleteImage(oldImageUrl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        }

        log.error("PRINT 2 newImageUrl " + newImageUrl);

        // Собираем DTO
        ActionDTO actionDTO = new ActionDTO();
        actionDTO.setId(id);
        actionDTO.setName(name);
        actionDTO.setDescription(description);
        actionDTO.setContent(content);
        actionDTO.setStartActionDate(startActionDate);
        actionDTO.setEndActionDate(endActionDate);
        actionDTO.setTitleImageUrl(newImageUrl);

        return ResponseEntity.ok(actionService.saveOrUpdate(actionDTO));

    }


}
