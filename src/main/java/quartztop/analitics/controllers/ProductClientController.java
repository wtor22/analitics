package quartztop.analitics.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quartztop.analitics.dtos.products.BundleDTO;
import quartztop.analitics.dtos.products.CategoryDTO;
import quartztop.analitics.httpClient.OkHttpClientSender;
import quartztop.analitics.services.crudProduct.CategoryCRUDService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/client/product")
@RequiredArgsConstructor
@Slf4j
public class ProductClientController {

    private final OkHttpClientSender httpClientSender;
    private final CategoryCRUDService categoryCRUDService;

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

}
