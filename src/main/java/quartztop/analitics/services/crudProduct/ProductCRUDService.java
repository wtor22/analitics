package quartztop.analitics.services.crudProduct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartztop.analitics.dtos.organizationData.CountriesDTO;
import quartztop.analitics.dtos.products.CategoryDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.models.organizationData.CountriesEntity;
import quartztop.analitics.models.products.CategoryEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.repositories.product.ProductRepository;
import quartztop.analitics.services.crudOrganization.CountriesCRUDService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCRUDService {

    private final ProductRepository productRepository;
    private final CategoryCRUDService categoryCRUDService;
    private final CountriesCRUDService countriesCRUDService;
    private final ProductAttributeService productAttributeService;

    @Transactional
    public ProductsEntity create(ProductDTO productDTO) {

        ProductsEntity productsEntity = mapToEntity(productDTO);

        productAttributeService.addAttributesToProduct(productsEntity, productDTO.getAttributes());

        if (productDTO.getCountry() != null ) {
            setCountry(productsEntity, productDTO.getCountry());
        } else {
            log.error("Country FOR product {} IS NULL", productDTO.getArticle());
        }

        if (productDTO.getCategoryDTO() != null) {
            setCategory(productsEntity, productDTO.getCategoryDTO());
        } else {
            log.error("Category FOR product {} IS NULL", productDTO.getArticle());
        }
        return productRepository.save(productsEntity);
    }

    public int updateOrCreateListProducts(List<ProductDTO> productDTOList) {

        List<ProductsEntity> productsEntityList = new ArrayList<>();
        for(ProductDTO productDTO : productDTOList) {
            ProductsEntity productsEntity = mapToEntity(productDTO);

            productAttributeService.addAttributesToProduct(productsEntity, productDTO.getAttributes());

            if (productDTO.getCountry() != null ) {
                setCountry(productsEntity, productDTO.getCountry());
            } else {
                log.error("Country FOR product {} IS NULL", productDTO.getArticle());
            }

            if (productDTO.getCategoryDTO() != null) {
                setCategory(productsEntity, productDTO.getCategoryDTO());
            } else {
                log.error("Category FOR product {} IS NULL", productDTO.getArticle());
            }

            productsEntityList.add(productsEntity);
        }
        productRepository.saveAll(productsEntityList);
        return productDTOList.size();
    }

    private void setCategory(ProductsEntity productsEntity, CategoryDTO categoryDTO) {
        Optional<CategoryEntity> optionalCategoryEntity = categoryCRUDService.getOptionalEntity(categoryDTO);
        if(optionalCategoryEntity.isEmpty()){
            CategoryEntity categoryEntity = categoryCRUDService.create(categoryDTO);
            productsEntity.setCategoryEntity(categoryEntity);
            return;
        }
        productsEntity.setCategoryEntity(optionalCategoryEntity.get());

    }

    public Optional<ProductsEntity> getOptionalEntityById(UUID id) {
        return productRepository.findById(id);
    }

    private void setCountry(ProductsEntity productsEntity, CountriesDTO countriesDTO) {
        Optional<CountriesEntity> optionalCountriesEntity = countriesCRUDService.getOptionalEntity(countriesDTO);

        if (optionalCountriesEntity.isEmpty()) {
            log.info("Country {} NOT FOUND", countriesDTO.getName());
            CountriesEntity countriesEntity = countriesCRUDService.create(countriesDTO);
            productsEntity.setCountries(countriesEntity);
            log.info("Countryn {} was created", countriesEntity.getName());
        } else {
            productsEntity.setCountries(optionalCountriesEntity.get());
        }
    }
    public Optional<ProductsEntity> getOptionalEntity(ProductDTO productDTO) {
        return productRepository.findById(productDTO.getId());
    }

    public List<UUID> findIdsNotInDb(List<UUID> inputIds) {
        List<UUID> existingIds = productRepository.findExistingIds(inputIds);
        return inputIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());
    }

    public static ProductDTO mapToDTO(ProductsEntity product) {

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setCode(product.getCode());
        productDTO.setArticle(product.getArticle());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPathName(product.getPathName());
        productDTO.setUpdated(product.getUpdated());

        return productDTO;
    }

    public static ProductsEntity mapToEntity(ProductDTO product) {

        ProductsEntity productsEntity = new ProductsEntity();
        productsEntity.setId(product.getId());
        productsEntity.setCode(product.getCode());
        productsEntity.setArticle(product.getArticle());
        productsEntity.setName(product.getName());
        productsEntity.setDescription(product.getDescription());
        productsEntity.setPathName(product.getPathName());
        productsEntity.setUpdated(product.getUpdated());

        return productsEntity;
    }


}
