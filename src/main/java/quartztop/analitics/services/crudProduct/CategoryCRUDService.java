package quartztop.analitics.services.crudProduct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.products.CategoryDTO;
import quartztop.analitics.models.products.CategoryEntity;
import quartztop.analitics.repositories.product.CategoryRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryCRUDService {

    private final CategoryRepository categoryRepository;


    public CategoryEntity create(CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = mapToEntity(categoryDTO);

        return categoryRepository.save(categoryEntity);
    }

    public void saveAll(List<CategoryEntity> categoryEntityList) {
        categoryRepository.saveAll(categoryEntityList);
    }

    public List<CategoryEntity> getAllEntity() {
        return categoryRepository.findAll();
    }

    public List<CategoryDTO> getAllEntitySortedByOrder() {
        List<CategoryEntity> categoryEntityList = categoryRepository.findAllOrdered();
        return categoryEntityList.stream().map(CategoryCRUDService::mapToDto).toList();
    }

    public List<CategoryDTO> getAllEntityWhereOrderIsNull() {
        List<CategoryEntity> categoryEntityList = categoryRepository.findAllNotOrdered();
        return categoryEntityList.stream().map(CategoryCRUDService::mapToDto).toList();
    }
    /**
     * Метод возвращает Отсортированную по алфавиту МАПУ с ключом PATH_NAME
     * и в value List с категориями
     */
    public Map<String,List<CategoryDTO>> getMapCategoryByPath() {

        List<CategoryEntity> categoryEntityList = categoryRepository.findAll();

        // Преобразуем в DTO
        List<CategoryDTO> categoryDTOS = categoryEntityList.stream()
                .map(CategoryCRUDService::mapToDto)
                .toList();

        // Собираем все pathName (уникальные названия групп)
        Set<String> groupNames = categoryDTOS.stream()
                .map(CategoryDTO::getPathName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Убираем категории, которые являются такими группами
        List<CategoryDTO> filteredCategories = categoryDTOS.stream()
                .filter(cat -> !groupNames.contains(cat.getName()))
                .toList();

        // Группировка по pathName и сортировка
        return filteredCategories.stream()
                .collect(Collectors.groupingBy(CategoryDTO::getPathName))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Возвращает MAP по группам категории которые участвуют в отчетах
     */
    public Map<String,List<CategoryDTO>> getMapCategoryByPathUsedInReport() {

        List<CategoryEntity> categoryEntityList = categoryRepository.findAllByUsedInReportsTrue();
        List<CategoryDTO> categoryDTOS = categoryEntityList.stream().map(CategoryCRUDService::mapToDto).toList();
        // Группировка по path
        Map<String, List<CategoryDTO>> grouped = categoryDTOS.stream()
                .collect(Collectors.groupingBy(CategoryDTO::getPathName));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // сортировка по алфавиту
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new // сохраняем порядок
                ));
    }

    public  boolean setUsedInReport(List<UUID> categoryUUIDList) {

        List<CategoryEntity> allCategoryEntities = categoryRepository.findAll();
        allCategoryEntities.forEach(c -> {
                if(categoryUUIDList.contains(c.getId())) {
                    c.setUsedInReports(true);
                } else {
                    c.setUsedInReports(false);
                }
        });
        return categoryRepository.saveAll(allCategoryEntities).isEmpty();
    }


    public Optional<CategoryEntity> getOptionalEntity(CategoryDTO categoryDTO) {
        return categoryRepository.findById(categoryDTO.getId());
    }
    public static CategoryEntity mapToEntity(CategoryDTO category) {

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setId(category.getId());
        categoryEntity.setDescription(category.getDescription());
        categoryEntity.setCode(category.getCode());
        categoryEntity.setName(category.getName());
        categoryEntity.setPathName(category.getPathName());
        categoryEntity.setUsedInReports(category.isUsedInReports());

        return categoryEntity;
    }

    public static CategoryDTO mapToDto(CategoryEntity category) {

        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setId(category.getId());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setCode(category.getCode());
        categoryDTO.setName(category.getName());
        categoryDTO.setPathName(category.getPathName());
        categoryDTO.setUsedInReports(category.isUsedInReports());

        return categoryDTO;
    }

}
