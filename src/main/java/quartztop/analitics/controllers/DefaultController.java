package quartztop.analitics.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import quartztop.analitics.services.crudOrganization.OwnerCRUDService;
import quartztop.analitics.services.crudProduct.CategoryCRUDService;

@Controller
@RequiredArgsConstructor
public class DefaultController {

    private final OwnerCRUDService ownerCRUDService;
    private final CategoryCRUDService categoryCRUDService;

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
}
