package quartztop.analitics.services.crudProduct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.dtos.organizationData.CountriesDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.models.organizationData.CountriesEntity;
import quartztop.analitics.models.products.BundleEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.repositories.product.BundleRepository;
import quartztop.analitics.services.crudOrganization.CountriesCRUDService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BundleCRUDService {

    private final BundleRepository bundleRepository;
    private final CountriesCRUDService countriesCRUDService;

    public BundleEntity create(BundleDTO bundleDTO) {
        BundleEntity bundleEntity = mapToEntity(bundleDTO);
        if (bundleDTO.getCountriesDTO() != null ) {
            setCountry(bundleEntity, bundleDTO.getCountriesDTO());
        } else {
            log.error("Country FOR product {} IS NULL", bundleDTO.getArticle());
        }
        if (!bundleDTO.getProductDTOList().isEmpty()) setListProducts(bundleEntity, bundleDTO.getProductDTOList());
        return bundleRepository.save(bundleEntity);
    }
    public Optional<BundleEntity> getOptionalEntity(BundleDTO bundleDTO) {
        return bundleRepository.findById(bundleDTO.getId());
    }

    private void setListProducts(BundleEntity bundleEntity, List<ProductDTO> productDTOList) {

        List<ProductsEntity> productsEntityList = productDTOList.stream().map(ProductCRUDService::mapToEntity).toList();
        bundleEntity.setProductsList(productsEntityList);
    }

    private void setCountry(BundleEntity bundleEntity, CountriesDTO countriesDTO) {
        Optional<CountriesEntity> optionalCountriesEntity = countriesCRUDService.getOptionalEntity(countriesDTO);

        if (optionalCountriesEntity.isEmpty()) {
            log.info("Country {} NOT FOUND", countriesDTO.getName());
            CountriesEntity countriesEntity = countriesCRUDService.create(countriesDTO);
            bundleEntity.setCountries(countriesEntity);
            log.info("Countryn {} was created", countriesEntity.getName());
        } else {
            bundleEntity.setCountries(optionalCountriesEntity.get());
        }
    }


    public static BundleEntity mapToEntity(BundleDTO bundle) {

        BundleEntity bundleEntity = new BundleEntity();
        bundleEntity.setId(bundle.getId());
        bundleEntity.setCode(bundle.getCode());
        bundleEntity.setArticle(bundle.getArticle());
        bundleEntity.setName(bundle.getName());
        bundleEntity.setDescription(bundle.getDescription());
        bundleEntity.setPathName(bundle.getPathName());
        return bundleEntity;
    }

    public static BundleDTO mapToDTO(BundleEntity bundle) {

        BundleDTO bundleDTO = new BundleDTO();
        bundleDTO.setId(bundle.getId());
        bundleDTO.setCode(bundle.getCode());
        bundleDTO.setArticle(bundle.getArticle());
        bundleDTO.setName(bundle.getName());
        bundleDTO.setDescription(bundle.getDescription());
        bundleDTO.setPathName(bundle.getPathName());
        return bundleDTO;
    }
}
