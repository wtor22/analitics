package quartztop.analitics.controllers.restApi;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import quartztop.analitics.reports.salesReportToExcel.ReportFromStock;
import quartztop.analitics.reports.salesReportToExcel.SalesReportDTO;
import quartztop.analitics.reports.salesReportToExcel.SalesReportToExelService;
import quartztop.analitics.reports.salesReportToExcel.StockByStoreAndCategoryDTO;
import quartztop.analitics.repositories.docsPositions.SalesReportRepository;
import quartztop.analitics.repositories.organizationData.OrganizationRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/bot/sales-report")
@RequiredArgsConstructor
@Slf4j
public class SalesReportController {

    private final SalesReportToExelService salesReportToExelService;
    private final SalesReportRepository salesReportRepository;
    private final OrganizationRepository organizationRepository;
    private final ReportFromStock report;
    private final ReportFromStock reportFromStock;

    @Value("${myapp.allowed-ip}")
    protected String allowedIp;

    @GetMapping("/stock")
    public ResponseEntity<List<StockByStoreAndCategoryDTO>> getReportStock(HttpServletRequest request) {
        return ResponseEntity.ok(report.getListDto());
    }


    @GetMapping("/get")
    public ResponseEntity<List<SalesReportDTO>> getSalesReport() {
        int year = 2025;
        LocalDateTime startOfYear;
        LocalDateTime endOfYear;

        startOfYear = LocalDate.of(year, 1, 1).atStartOfDay();
        endOfYear = LocalDate.of(year, 12, 31).atTime(23,59,59,999);
        List<UUID> orgIds = organizationRepository.findAllId();
        return ResponseEntity.ok(salesReportRepository.getSalesReport(startOfYear,endOfYear, orgIds));
    }

    @GetMapping("/stock/download")
    public ResponseEntity<Resource> downloadStockReport(@RequestParam(required = false) Integer year,
                                                         HttpServletRequest request) {

        String remoteAddr =  request.getRemoteAddr();

        if (!isIpAllowed(remoteAddr, allowedIp) && (!allowedIp.equals("0.0.0.0/0"))) {
            log.warn("🚫 Доступ запрещён для IP: {}", remoteAddr);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Workbook workbook = reportFromStock.createExcelBookReportStockProducts();
        Resource resource;
        try {
            resource = createExcelResource(workbook);
        } catch (IOException e) {
            log.error("Ошибка при создании ресурса: ", e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filename"  + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

    /**
     *
     * @param year Год отчета
     * @param type Тип отчета general основные поставщики inter_stone ИнтерСтоун
     */
    @GetMapping("/rating/download")
    public ResponseEntity<Resource> downloadRatingReport(@RequestParam(required = false) Integer year,
                                                         @RequestParam(required = false) String type,
                                                         HttpServletRequest request) {

        String remoteAddr =  request.getRemoteAddr();

        if (!isIpAllowed(remoteAddr, allowedIp) && (!allowedIp.equals("0.0.0.0/0"))) {
            log.warn("🚫 Доступ запрещён для IP: {}", remoteAddr);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Workbook workbook = salesReportToExelService.createExcelBookReportRatingProducts(year, type);
        Resource resource;
        try {
            resource = createExcelResource(workbook);
        } catch (IOException e) {
            log.error("Ошибка при создании ресурса: ", e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filename"  + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

    /**
     *
     * @param year Год отчета
     * @param type Тип отчета general основные поставщики inter_stone ИнтерСтоун
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam(required = false) Integer year,
                                                   @RequestParam(required = false) String type,
                                                   HttpServletRequest request) {

        String remoteAddr =  request.getRemoteAddr();

        if (!isIpAllowed(remoteAddr, allowedIp) && (!allowedIp.equals("0.0.0.0/0"))) {
            log.warn("🚫 Доступ запрещён для IP: {}", remoteAddr);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Workbook workbook = salesReportToExelService.createExcelBookReportOrders(year, type);
        Resource resource;
        try {
            resource = createExcelResource(workbook);
        } catch (IOException e) {
            log.error("Ошибка при создании ресурса: ", e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filename"  + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public Resource createExcelResource(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close(); // Закрываем workbook после записи в поток
            return new ByteArrayResource(out.toByteArray());
        }
    }

    private boolean isIpAllowed(String ip, String subnet) {
        try {
            String[] parts = subnet.split("/");
            String subnetIp = parts[0];
            int prefix = Integer.parseInt(parts[1]);

            InetAddress subnetAddress = InetAddress.getByName(subnetIp);
            InetAddress remoteAddress = InetAddress.getByName(ip);

            byte[] subnetBytes = subnetAddress.getAddress();
            byte[] remoteBytes = remoteAddress.getAddress();

            int mask = ~((1 << (32 - prefix)) - 1);

            int subnetInt = ByteBuffer.wrap(subnetBytes).getInt();
            int remoteInt = ByteBuffer.wrap(remoteBytes).getInt();

            return (subnetInt & mask) == (remoteInt & mask);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

}
