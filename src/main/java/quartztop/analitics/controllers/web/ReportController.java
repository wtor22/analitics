package quartztop.analitics.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import quartztop.analitics.dtos.counterparty.GroupAgentDTO;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.GroupAgentEntity;
import quartztop.analitics.models.docsPositions.DemandPositionsEntity;
import quartztop.analitics.models.organizationData.OwnerEntity;
import quartztop.analitics.reports.GeneralReportsDTO;
import quartztop.analitics.reports.ReportService;
import quartztop.analitics.services.counterparty.GroupAgentCRUDService;
import quartztop.analitics.services.crudDemandPositions.DemandPositionCRUDService;
import quartztop.analitics.services.crudOrganization.OwnerCRUDService;
import quartztop.analitics.services.crudProduct.CategoryCRUDService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/client/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    private final ReportService reportService;
    private final OwnerCRUDService ownerCRUDService;
    private final CategoryCRUDService categoryCRUDService;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam UUID managerId,
                                                   @RequestParam List<Integer> listIdTags,
                                                   @RequestParam LocalDate startPeriod,
                                                   @RequestParam LocalDate endPeriod,
                                                   @RequestParam LocalDate comparisonPeriodStart,
                                                   @RequestParam LocalDate comparisonPeriodEnd,
                                                   @RequestParam List<UUID> listUUIDCategory,
                                                   @RequestParam  boolean isRememberCategorySelection) {


        Optional<OwnerEntity> optionalOwnerEntity = ownerCRUDService.getOptionalEntity(managerId);
        if (optionalOwnerEntity.isEmpty()) return ResponseEntity.notFound().build();
        String nameFile = "report-" + optionalOwnerEntity.get().getUid() + "|" + startPeriod + "-" + endPeriod;
        GeneralReportsDTO reportDto = reportService.createReportByGroupAgentsAndCategories(managerId, listIdTags,
                startPeriod, endPeriod,
                comparisonPeriodStart, comparisonPeriodEnd, listUUIDCategory);

        Workbook workbook = reportService.createExcelSheetReportOrders(reportDto);

        if (isRememberCategorySelection) {
            boolean isSetting = categoryCRUDService.setUsedInReport(listUUIDCategory);
            if(isSetting) log.info("SET REMEMBER CATEGORY SUCCESSFULLY");
        }
        Resource resource;

        try {
            resource = createExcelResource(workbook);
        } catch (IOException e) {
            log.error("Ошибка при создании ресурса: ", e);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nameFile + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/by-tags")
    public ResponseEntity<GeneralReportsDTO> downloadExelReport(@RequestParam UUID managerId,
                                                                @RequestParam List<Integer> listIdTags,
                                                                @RequestParam LocalDate startPeriod,
                                                                @RequestParam LocalDate endPeriod,
                                                                @RequestParam LocalDate comparisonPeriodStart,
                                                                @RequestParam LocalDate comparisonPeriodEnd,
                                                                @RequestParam List<UUID> listUUIDCategories)
            throws IOException {


        GeneralReportsDTO generalReportsDTO = reportService.createReportByGroupAgentsAndCategories(managerId, listIdTags,
                startPeriod, endPeriod,
                comparisonPeriodStart, comparisonPeriodEnd, listUUIDCategories);


        return ResponseEntity.ok(generalReportsDTO);
    }


    public Resource createExcelResource(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close(); // Закрываем workbook после записи в поток
            return new ByteArrayResource(out.toByteArray());
        }
    }

}
