package quartztop.analitics.services.crudProduct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.products.CategoryDTO;
import quartztop.analitics.models.products.CategoryEntity;
import quartztop.analitics.repositories.product.CategoryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryCRUDService {

    private final CategoryRepository categoryRepository;


    public CategoryEntity create(CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = mapToEntity(categoryDTO);
        return categoryRepository.save(categoryEntity);
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

        return categoryEntity;
    }

    public static CategoryDTO mapToDto(CategoryEntity category) {

        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setId(category.getId());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setCode(category.getCode());
        categoryDTO.setName(category.getName());
        categoryDTO.setPathName(category.getPathName());

        return categoryDTO;
    }

}
