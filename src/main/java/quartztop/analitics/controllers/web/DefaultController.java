package quartztop.analitics.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import quartztop.analitics.dtos.products.CategoryDTO;
import quartztop.analitics.models.products.CategoryEntity;
import quartztop.analitics.services.crudOrganization.OwnerCRUDService;
import quartztop.analitics.services.crudProduct.CategoryCRUDService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DefaultController {

    private final OwnerCRUDService ownerCRUDService;
    private final CategoryCRUDService categoryCRUDService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/logout")
    public String logoutPage() {
        return "logout";
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("groupedCategories", categoryCRUDService.getMapCategoryByPath());
        model.addAttribute("listOwners", ownerCRUDService.getListOwnerByUsedInReportTrue());
        return "index";
    }

    @RequestMapping("/settings")
    public String settings(Model model) {

        model.addAttribute("listOwners", ownerCRUDService.getListOwnersDTO());
        model.addAttribute("groupedCategories", categoryCRUDService.getMapCategoryByPath());

        return "settings";
    }

    @RequestMapping("/bot-settings")
    public String botSetting(Model model) {

        List<CategoryEntity> notSelectedCategories = categoryCRUDService.getAllEntity();
        List<CategoryDTO> selectedCategories = categoryCRUDService.getAllEntityWhereOrderIsNull();
        log.warn("SIZE LIST CATEGORIES " + notSelectedCategories.size());
        model.addAttribute("categoriesIsPresent", notSelectedCategories);
        model.addAttribute("selectedCategories", selectedCategories);

        return "bot-settings";
    }
}
