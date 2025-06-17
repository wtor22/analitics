package quartztop.analitics.controllers.restApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.CategoryDTO;
import quartztop.analitics.dtos.products.ProductDTO;
import quartztop.analitics.handlers.BundleHandler;
import quartztop.analitics.handlers.ProductHandler;
import quartztop.analitics.integration.mySkladIntegration.MySkladClient;
import quartztop.analitics.responses.stock.categoryResponse.CategoryResponse;
import quartztop.analitics.models.products.CategoryEntity;
import quartztop.analitics.services.crudProduct.CategoryCRUDService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/client/product")
@RequiredArgsConstructor
@Slf4j
public class ProductClientController {

    private final MySkladClient httpClientSender;
    private final CategoryCRUDService categoryCRUDService;
    private final ProductHandler productHandler;
    private final BundleHandler bundleHandler;

    @GetMapping("/all")
    public ResponseEntity<String> updateAllProductAndBundle() {

        if(productHandler.checkExecution()) return ResponseEntity.ok("Жди!!! Обновляются товары");
        if(bundleHandler.checkExecution()) return ResponseEntity.ok("Жди!!! Обновляются комплекты");
        String responseProduct = productHandler.downloadProductsWithOffset();
        String responseBundle = bundleHandler.downloadBundlesWithOffset();
        String response = responseProduct + "<br>" + responseBundle;
        return ResponseEntity.ok(response);
    }


    // Получаем все продукты из МС
    @GetMapping
    public ResponseEntity<String> getProductsList() {
        String response = productHandler.downloadProductsWithOffset();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/bundle")
    public ResponseEntity<String> getBundleList() {
        String response = bundleHandler.downloadBundlesWithOffset();
        return ResponseEntity.ok(response);
    }
    // Ничего не сохраняет в БД. Чисто для теста
    @GetMapping("/bundle/{id}")
    public ResponseEntity<BundleDTO> bundleById(@PathVariable UUID id) {
        log.info("GET BUNDLE REQUEST WITH ID " + id);
        BundleDTO bundleDTO = httpClientSender.getBundle(id);
        return ResponseEntity.ok(bundleDTO);
    }

    @GetMapping("/category")
    public ResponseEntity<Map<String, List<CategoryDTO>>> getCategories() {
        return ResponseEntity.ok(categoryCRUDService.getMapCategoryByPath());
    }
    @GetMapping("/category/getOrdered")
    public ResponseEntity<List<CategoryDTO>> getOrderedCategories() {
        log.info("GET ORDERED");
        return ResponseEntity.ok(categoryCRUDService.getAllEntitySortedByOrder());
    }

    @GetMapping("/category/getAvailable")
    public ResponseEntity<CategoryResponse> getNotOrderedCategories() {

        List<CategoryDTO> listCategoryIsPresent = categoryCRUDService.getAllEntitySortedByOrder();
        List<CategoryDTO> listCategoryNotPresent = categoryCRUDService.getAllEntityWhereOrderIsNull();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategoriesIsPresent(listCategoryIsPresent);
        categoryResponse.setCategoriesNotPresent(listCategoryNotPresent);

        return ResponseEntity.ok(categoryResponse);
    }

    @PostMapping("/category/setOrdered")
    public ResponseEntity<CategoryResponse> setCategoriesOrderInBotIndex(@RequestBody List<UUID> sortedListCategoryUUID) {

        List<CategoryEntity> allListCategory = categoryCRUDService.getAllEntity();
        for(CategoryEntity category : allListCategory) {

            if(sortedListCategoryUUID.contains(category.getId())) {
                int index = sortedListCategoryUUID.indexOf(category.getId()) + 1;
                category.setOrderInBotIndex(index);
            } else {
                category.setOrderInBotIndex(0);
            }
        }
        categoryCRUDService.saveAll(allListCategory);

        List<CategoryDTO> listCategoryIsPresent = categoryCRUDService.getAllEntitySortedByOrder();
        List<CategoryDTO> listCategoryNotPresent = categoryCRUDService.getAllEntityWhereOrderIsNull();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategoriesIsPresent(listCategoryIsPresent);
        categoryResponse.setCategoriesNotPresent(listCategoryNotPresent);

        return ResponseEntity.ok(categoryResponse);
    }

}
