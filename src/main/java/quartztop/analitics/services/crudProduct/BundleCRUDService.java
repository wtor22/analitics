package quartztop.analitics.services.crudProduct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartztop.analitics.dtos.organizationData.CountriesDTO;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.models.organizationData.CountriesEntity;
import quartztop.analitics.models.products.BundleEntity;
import quartztop.analitics.models.products.BundleProduct;
import quartztop.analitics.models.products.BundleProductId;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.repositories.product.BundleRepository;
import quartztop.analitics.services.crudOrganization.CountriesCRUDService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BundleCRUDService {

    private final BundleRepository bundleRepository;
    private final ProductCRUDService productCRUDService;
    private final CountriesCRUDService countriesCRUDService;

    @Transactional
    public BundleEntity create(BundleDTO bundleDTO) {
        BundleEntity bundleEntity = mapToEntity(bundleDTO);
        if (bundleDTO.getCountry() != null ) {
            setCountry(bundleEntity, bundleDTO.getCountry());
        } else {
            log.error("Country FOR product {} IS NULL", bundleDTO.getArticle());
        }
        if (!bundleDTO.getProductDTOList().isEmpty()) {
            setListProducts(bundleEntity, bundleDTO.getProductDTOList());
        }
        return bundleRepository.save(bundleEntity);
    }

    @Transactional
    public void updateOrCreateBundleList(List<BundleDTO> bundleDTOS) {

        List<BundleEntity> bundleEntities = new ArrayList<>();

        for(BundleDTO bundleDTO : bundleDTOS) {
            Optional<BundleEntity> optionalBundleEntity = bundleRepository.findById(bundleDTO.getId());
            BundleEntity bundleEntity;

            if (optionalBundleEntity.isEmpty()) {
                bundleEntity = mapToEntity(bundleDTO);
            } else {
                bundleEntity = optionalBundleEntity.get();
                bundleEntity.setUpdated(bundleDTO.getUpdated());
                bundleEntity.setName(bundleDTO.getName());
                bundleEntity.setDescription(bundleDTO.getDescription());
                bundleEntity.setCode(bundleDTO.getCode());
                bundleEntity.setArticle(bundleDTO.getArticle());
                bundleEntity.setPathName(bundleDTO.getPathName());
            }

            if (bundleDTO.getCountry() != null ) {
                setCountry(bundleEntity, bundleDTO.getCountry());
            } else {
                log.error("Country FOR product {} IS NULL", bundleDTO.getArticle());
            }
            if (!bundleDTO.getProductDTOList().isEmpty()) {
                setListProducts(bundleEntity, bundleDTO.getProductDTOList());
            }
            bundleEntities.add(bundleEntity);
        }

        bundleRepository.saveAll(bundleEntities);
    }
    public Optional<BundleEntity> getOptionalEntity(BundleDTO bundleDTO) {
        return bundleRepository.findById(bundleDTO.getId());
    }

    private void setListProducts(BundleEntity bundleEntity, List<ProductDTO> productDTOList) {
        for(ProductDTO productDTO: productDTOList) {
            Optional<ProductsEntity> optionalProductsEntity = productCRUDService.getOptionalEntity(productDTO);

            ProductsEntity productEntity;

            productEntity = optionalProductsEntity.orElseGet(() -> productCRUDService.create(productDTO));

            // Проверяем, есть ли уже BundleProduct с этим продуктом
            Optional<BundleProduct> existingBundleProduct = bundleEntity.getBundleProducts().stream()
                    .filter(bp -> bp.getProduct().getId().equals(productEntity.getId()))
                    .findFirst();

            if (existingBundleProduct.isPresent()) {
                // Обновляем количество
                existingBundleProduct.get().setQuantity(productDTO.getQuantity());
            } else {
                // Создаем новый связанный BundleProduct
                BundleProduct newBundleProduct = new BundleProduct();
                newBundleProduct.setBundle(bundleEntity);
                newBundleProduct.setProduct(productEntity);
                newBundleProduct.setQuantity(productDTO.getQuantity());

                // Устанавливаем composite id
                BundleProductId id = new BundleProductId();
                id.setBundleId(bundleEntity.getId());
                id.setProductId(productEntity.getId());
                newBundleProduct.setId(id);

                bundleEntity.getBundleProducts().add(newBundleProduct);
            }
        }
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
        bundleEntity.setUpdated(bundle.getUpdated());

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
        bundleDTO.setUpdated(bundle.getUpdated());
        return bundleDTO;
    }


}
