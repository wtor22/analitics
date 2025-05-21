package quartztop.analitics.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import quartztop.analitics.dtos.products.CategoryDTO;
import quartztop.analitics.integration.botApiResponses.BuilderBotResponse;
import quartztop.analitics.integration.botApiResponses.ButtonDto;
import quartztop.analitics.integration.botApiResponses.StatisticsResponses;
import quartztop.analitics.models.organizationData.Organization;
import quartztop.analitics.models.products.CategoryEntity;
import quartztop.analitics.services.crudOrganization.OrganizationCRUDService;
import quartztop.analitics.services.crudOrganization.OwnerCRUDService;
import quartztop.analitics.services.crudProduct.CategoryCRUDService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DefaultController {

    private final OwnerCRUDService ownerCRUDService;
    private final CategoryCRUDService categoryCRUDService;
    private final BuilderBotResponse builderBotResponse;
    private final BuilderBotResponse builderStatisticsResponse;
    private final OrganizationCRUDService organizationCRUDService;

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
    @RequestMapping("/statistics")
    public String statistics(Model model) {
        model.addAttribute("groupedCategories", categoryCRUDService.getMapCategoryByPath());
        model.addAttribute("listOwners", ownerCRUDService.getListOwnerByUsedInReportTrue());
        return "statistics";
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
        List<ButtonDto> buttonDtoList = builderBotResponse.listButtonsResponse();

        model.addAttribute("botImageButtonList", buttonDtoList);
        model.addAttribute("categoriesIsPresent", notSelectedCategories);
        model.addAttribute("selectedCategories", selectedCategories);

        return "bot-settings";
    }

    @RequestMapping("/actions")
    public String actions(Model model) {
        List<Organization> organizations = organizationCRUDService.getAll();
        model.addAttribute("organizations",organizations);
        return "actions";
    }


    @RequestMapping("/bot-statistics")
    public String botStatistics(Model model) {
        StatisticsResponses statisticsResponses = builderStatisticsResponse.statisticsResponses();
        if (statisticsResponses == null) {
            model.addAttribute("errorMessage", "Сервис бота временно недоступен.");
        } else {
            model.addAttribute("statisticsResponses", statisticsResponses);
        }

        return "bot-statistics";
    }
}
