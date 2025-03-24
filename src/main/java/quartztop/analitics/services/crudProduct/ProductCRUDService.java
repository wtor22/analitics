package quartztop.analitics.services.crudProduct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.CountriesDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.models.organizationData.CountriesEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.repositories.product.ProductRepository;
import quartztop.analitics.services.crudOrganization.CountriesCRUDService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCRUDService {

    private final ProductRepository productRepository;
    private final CountriesCRUDService countriesCRUDService;

    public ProductsEntity create(ProductDTO productDTO) {

        ProductsEntity productsEntity = mapToEntity(productDTO);

        if (productDTO.getCountry() != null ) {
            setCountry(productsEntity, productDTO.getCountry());
        } else {
            log.error("Country FOR product {} IS NULL", productDTO.getArticle());
        }
        return productRepository.save(productsEntity);
    }

    public Optional<ProductsEntity> getOptionalEntity(ProductDTO productDTO) {
        return productRepository.findById(productDTO.getId());
    }
    public ProductDTO getProductDto(UUID id) {
        Optional<ProductsEntity> optionalProductsEntity = productRepository.findById(id);
        return optionalProductsEntity.map(ProductCRUDService::mapToDTO).orElse(null);

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

    public static ProductDTO mapToDTO(ProductsEntity product) {

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setCode(product.getCode());
        productDTO.setArticle(product.getArticle());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPathName(product.getPathName());

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

        return productsEntity;
    }
}
