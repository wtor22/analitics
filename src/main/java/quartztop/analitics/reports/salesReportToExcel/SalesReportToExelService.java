package quartztop.analitics.reports.salesReportToExcel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import quartztop.analitics.repositories.docsPositions.SalesReportRepository;
import quartztop.analitics.repositories.organizationData.OrganizationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalesReportToExelService {

    private final SalesReportRepository salesReportRepository;
    private final OrganizationRepository organizationRepository;

    private final String[] headersColumn = {"Месяц","Belenco","Stratos","Coante","Italstone","Materia","Fondovalle","Ascale", "Слэбов по организации", "Слэбов всего"};
    private final String[] headersCategoryColumn = {"Belenco","Stratos","Coante","Italstone","Materia","Fondovalle","Ascale"};
    private final String[] categoriesNames = {"Belenco","Stratos","Coante","Italstone","Materia","Fondovalle","Ascale"};
    private final String[] ratingHeaderColumn = {"№", "Товар", "Кол-во проданных", "Остаток на складах"};

    public Workbook createExcelBookReportRatingProducts(Integer year) {

        Workbook workbook = new XSSFWorkbook();

        // Создаем стиль для ячеек
        CellStyle headerStyle = CellStyles.createHeaderCellStyle(workbook);

        // Формирую даты конца и начала отчета
        LocalDateTime startOfYear;
        LocalDateTime endOfYear;
        if(year != null) {
            startOfYear = LocalDate.of(year, 1, 1).atStartOfDay();
            endOfYear = LocalDate.of(year, 12, 31).atTime(23,59,59,999);
        } else {
            startOfYear = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0);
            endOfYear = LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59, 999);
        }

        List<UUID> orgIds = organizationRepository.findAllId();
        List<RatingProductReportDTO> listReportDTO =
                salesReportRepository.getRatingProductReport(startOfYear, endOfYear, orgIds);

        // Если список пустой
        if (listReportDTO.isEmpty()) {
            int rowIndex = 1; // Индекс первого ряда
            XSSFSheet sheet = (XSSFSheet) workbook.createSheet("Лист1");
            String headerSheet = " ДАННЫХ ПО ТОВАРАМ НЕ НАЙДЕНО ";
            Row titleRow = sheet.createRow(rowIndex);
            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),titleRow.getRowNum(),0,7));
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(headerSheet);
            titleCell.setCellStyle(headerStyle);
            return workbook;
        }
        String periodToHeader = endOfYear.getYear() + " год";
        for(String category: categoriesNames) {
            createSheetRatingPerCategory(workbook, category, periodToHeader, listReportDTO);
        }
        return workbook;
    }


    public Workbook createExcelBookReportOrders(Integer year) {

        Workbook workbook = new XSSFWorkbook();

        // Создаем стиль для ячеек
        CellStyle headerStyle = CellStyles.createHeaderCellStyle(workbook);

        // Формирую даты конца и начала отчета
        LocalDateTime startOfYear;
        LocalDateTime endOfYear;
        if(year != null) {
            startOfYear = LocalDate.of(year, 1, 1).atStartOfDay();
            endOfYear = LocalDate.of(year, 12, 31).atTime(23,59,59,999);
        } else {
            startOfYear = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0);
            endOfYear = LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59, 999);
        }

        List<UUID> orgIds = organizationRepository.findAllId();
        List<SalesReportDTO> listReportDTO = salesReportRepository.getSalesReport(startOfYear, endOfYear, orgIds);
        if (listReportDTO.isEmpty()) {
            int rowIndex = 1; // Индекс первого ряда
            XSSFSheet sheet = (XSSFSheet) workbook.createSheet("Лист1");
            String headerSheet = " ОТГРУЗОК НЕ НАЙДЕНО ";
            Row titleRow = sheet.createRow(rowIndex);
            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),titleRow.getRowNum(),0,7));
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(headerSheet);
            titleCell.setCellStyle(headerStyle);
            return workbook;
        }
        List<String> organizations = listReportDTO
                .stream()
                .map(SalesReportDTO::getOrganizationName)
                .distinct()
                .toList();

        String periodToHeader = getPeriodAsString(listReportDTO);

        for(String org: organizations) {
            if (org.toLowerCase().contains("кварцтоп")) {
                // Кварцтоп отчет с суммами
                createSheetPerOrgWithSum(workbook, org, periodToHeader, listReportDTO);
                continue;
            }
            createSheetPerOrg(workbook, org, periodToHeader, listReportDTO);
        }

        // передаем org null --- значит отчет по категориям без учета организации
        createSheetPerOrg(workbook, null, periodToHeader, listReportDTO);
        return workbook;
    }

    private void createSheetRatingPerCategory(Workbook workbook, String categoryName,  String periodToHeader, List<RatingProductReportDTO> listReportDTO) {
        // Создаем стиль для ячеек
        CellStyle headerStyle = CellStyles.createHeaderCellStyle(workbook);
        CellStyle cellStyle = CellStyles.createCellStyle(workbook);
        CellStyle subHeaderStyle = CellStyles.createSubHeaderCellStyle(workbook);

        int rowIndex = 1; // Индекс первого ряда
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet( categoryName );

        String headerSheet =  categoryName + "   Рейтинг продаж за " +  periodToHeader;

        Row titleRow = sheet.createRow(rowIndex++);
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),titleRow.getRowNum(),1,7));
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue(headerSheet);
        titleCell.setCellStyle(headerStyle);

        Row rowColumnHeader = sheet.createRow(rowIndex++);
        for(int i = 0; i < ratingHeaderColumn.length; i++) {
            Cell cell = rowColumnHeader.createCell(i + 1);
            cell.setCellValue(ratingHeaderColumn[i]);
            cell.setCellStyle(subHeaderStyle);
        }

        int rating = 1;
        for(RatingProductReportDTO ratingProductReportDTO: listReportDTO) {

            if (!ratingProductReportDTO.getCategoryName().contains(categoryName)) continue;

            Row dataRow = sheet.createRow(rowIndex ++);

            Cell ratingCell = dataRow.createCell(1);
            ratingCell.setCellValue(rating++);
            ratingCell.setCellStyle(CellStyles.createCellStyleRating(workbook));

            Cell productNameCell = dataRow.createCell(2);
            productNameCell.setCellValue(ratingProductReportDTO.getProductName());
            productNameCell.setCellStyle(cellStyle);

            Cell dataCell = dataRow.createCell(3);
            dataCell.setCellValue(ratingProductReportDTO.getTotalQuantity());
            dataCell.setCellStyle(cellStyle);

            Cell dataStock = dataRow.createCell(4);

            dataStock.setCellValue(ratingProductReportDTO.getStock());
            dataStock.setCellStyle(cellStyle);

        }
        for (int i = 0; i <= ratingHeaderColumn.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Устанавливаю ширину колонок
        sheet.setColumnWidth(0, 2 * 256);
        sheet.setColumnWidth(1, 5 * 256);
    }

    private void createSheetPerOrgWithSum(Workbook workbook, String org, String periodToHeader, List<SalesReportDTO> listReportDTO) {

        // Создаем стиль для ячеек
        CellStyle headerStyle = CellStyles.createHeaderCellStyle(workbook);
        CellStyle cellStyle = CellStyles.createCellStyle(workbook);
        CellStyle totalStyle = CellStyles.createTotalStyle(workbook);
        CellStyle moneyStyle = CellStyles.createCellStyle(workbook);
        CellStyle moneyTotalStyle = CellStyles.createTotalStyle(workbook);

        DataFormat format = workbook.createDataFormat();
        moneyTotalStyle.setDataFormat(format.getFormat("#,##0.00")); // разделитель тысяч <,>
        moneyStyle.setDataFormat(format.getFormat("#,##0.00"));
        int rowIndex = 1; // Индекс первого ряда
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(org);

        String headerSheet = org + " Отгрузки " + periodToHeader;
        Row titleRow = sheet.createRow(rowIndex++);
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),titleRow.getRowNum(),1,7));
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue(headerSheet);
        titleCell.setCellStyle(headerStyle);
        Row separatorRow  = sheet.createRow(rowIndex++);
        separatorRow.setHeightInPoints(5f);

        Row rowColumnHeader = sheet.createRow(rowIndex++);
        Row rowUnderColumnHeader = sheet.createRow(rowIndex++);
        int indexColl = 1;
        int indexUnderCol = 2;
        sheet.addMergedRegion(new CellRangeAddress(rowColumnHeader.getRowNum(), rowUnderColumnHeader.getRowNum(), indexColl, indexColl));
        Cell monthTitleCell = rowColumnHeader.createCell(indexColl);
        Cell mergedMonthTitleCell = rowUnderColumnHeader.createCell(indexColl++);
        monthTitleCell.setCellValue("Месяц");
        monthTitleCell.setCellStyle(CellStyles.createMonthSubHeader(workbook));
        mergedMonthTitleCell.setCellStyle(CellStyles.createMonthSubHeader(workbook));

        CellStyle subHeaderStyle = CellStyles.createSubHeaderCellStyle(workbook);
        for(int i = 0; i < headersCategoryColumn.length; i++) {

            sheet.addMergedRegion(new CellRangeAddress(rowColumnHeader.getRowNum(), rowColumnHeader.getRowNum(),indexColl + i,indexColl + i + 1));
            Cell firstCell = rowColumnHeader.createCell((indexColl) + i);
            Cell secondCell = rowColumnHeader.createCell((indexColl + 1) + i);
            firstCell.setCellValue(headersCategoryColumn[i]);
            firstCell.setCellStyle(subHeaderStyle);
            secondCell.setCellStyle(subHeaderStyle);
            Cell underQuantity = rowUnderColumnHeader.createCell(indexUnderCol ++);
            underQuantity.setCellValue("  Кол-во");
            Cell underSum = rowUnderColumnHeader.createCell(indexUnderCol ++);
            underSum.setCellValue("Рублей");
            indexColl = indexColl + 1;
            underQuantity.setCellStyle(subHeaderStyle);
            underSum.setCellStyle(subHeaderStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(rowColumnHeader.getRowNum(), rowUnderColumnHeader.getRowNum(), indexUnderCol, indexUnderCol));
        Cell totalPerOrgTitleCell = rowColumnHeader.createCell(indexUnderCol);
        Cell mergedTotalPerOrgTitleCell = rowUnderColumnHeader.createCell(indexUnderCol);
        indexUnderCol++;
        totalPerOrgTitleCell.setCellValue("Слэбов по организации");
        totalPerOrgTitleCell.setCellStyle(subHeaderStyle);
        mergedTotalPerOrgTitleCell.setCellStyle(subHeaderStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowColumnHeader.getRowNum(), rowUnderColumnHeader.getRowNum(), indexUnderCol, indexUnderCol));
        Cell totalSumPerOrgTitleCell = rowColumnHeader.createCell(indexUnderCol);
        Cell mergedTotalSumPerOrgTitleCell = rowUnderColumnHeader.createCell(indexUnderCol);
        indexUnderCol++;
        totalSumPerOrgTitleCell.setCellValue("Рублей по организации");
        totalSumPerOrgTitleCell.setCellStyle(subHeaderStyle);
        mergedTotalSumPerOrgTitleCell.setCellStyle(subHeaderStyle);


        sheet.addMergedRegion(new CellRangeAddress(rowColumnHeader.getRowNum(), rowUnderColumnHeader.getRowNum(), indexUnderCol, indexUnderCol));
        Cell totalTitleCell = rowColumnHeader.createCell(indexUnderCol);
        Cell mergedTotalTitleCell = rowUnderColumnHeader.createCell(indexUnderCol);
        totalTitleCell.setCellValue("Слэбов всего");
        totalTitleCell.setCellStyle(subHeaderStyle);
        mergedTotalTitleCell.setCellStyle(subHeaderStyle);

        Map<String, Float[]> mapDataPerMonth = getMapDataPerMonthWithSum(listReportDTO,headersCategoryColumn,org);

        for(Map.Entry<String, Float[]> entry: mapDataPerMonth.entrySet()) {
            boolean isTotalRow = entry.getKey().equals("Итого");

            Row dataRow = sheet.createRow(rowIndex ++);
            Cell monthCell = dataRow.createCell(1);
            monthCell.setCellValue(entry.getKey());

            if(isTotalRow) {
                monthCell.setCellStyle(totalStyle);
            } else {
                monthCell.setCellStyle(cellStyle);
            }
            Float[] data = entry.getValue();
            for(int i = 0; i < data.length - 3; i += 2) {
                float quantity = data[i] != null ? data[i] : 0;
                float sum = data[i + 1] != null ? data[i + 1] : 0;

                Cell dataQauntityCell = dataRow.createCell(i + 2);
                Cell dataSumCell = dataRow.createCell(i + 3);

                dataQauntityCell.setCellValue(quantity);
                dataSumCell.setCellValue(sum);

                if(isTotalRow) {
                    dataQauntityCell.setCellStyle(totalStyle);
                    dataSumCell.setCellStyle(moneyTotalStyle);
                } else {
                    dataQauntityCell.setCellStyle(cellStyle);
                    dataSumCell.setCellStyle(moneyStyle);
                }
            }
            Cell dataRowCellQuantityPerMonthAndOrg = dataRow.createCell(indexUnderCol - 2);
            dataRowCellQuantityPerMonthAndOrg.setCellValue(data[data.length - 3]);


            Cell dataRowCellSumPerMonthAndOrg = dataRow.createCell(indexUnderCol - 1);
            dataRowCellSumPerMonthAndOrg.setCellValue(data[data.length - 2]);


            Cell dataRowCellQuantityPerMonth = dataRow.createCell(indexUnderCol );
            dataRowCellQuantityPerMonth.setCellValue(data[data.length - 1]);

            if(isTotalRow) {
                dataRowCellQuantityPerMonthAndOrg.setCellStyle(totalStyle);
                dataRowCellSumPerMonthAndOrg.setCellStyle(moneyTotalStyle);
                dataRowCellQuantityPerMonth.setCellStyle(totalStyle);
            } else {
                dataRowCellQuantityPerMonthAndOrg.setCellStyle(cellStyle);
                dataRowCellSumPerMonthAndOrg.setCellStyle(moneyStyle);
                dataRowCellQuantityPerMonth.setCellStyle(cellStyle);
            }
        }

        // Автоматическое выравнивание колонок
        for (int i = 1; i <= indexUnderCol; i++) {
            if (i % 2 == 0 ) {
                sheet.setColumnWidth(1, 8 * 256);
                continue;
            }
            sheet.setColumnWidth(i, 14 * 256);
            //sheet.autoSizeColumn(i);
        }
        // Устанавливаю ширину колонок
        sheet.setColumnWidth(0, 2 * 256);
        sheet.setColumnWidth(1, 11 * 256);
        sheet.setColumnWidth(indexUnderCol - 2, 14 * 256);
        sheet.setColumnWidth(indexUnderCol - 1, 16 * 256);
        sheet.setColumnWidth(indexUnderCol, 11 * 256);
    }

    private void createSheetPerOrg(Workbook workbook, String org, String periodToHeader, List<SalesReportDTO> listReportDTO) {

        // Создаем стиль для ячеек
        CellStyle headerStyle = CellStyles.createHeaderCellStyle(workbook);
        CellStyle cellStyle = CellStyles.createCellStyle(workbook);
        CellStyle subHeaderStyle = CellStyles.createSubHeaderCellStyle(workbook);
        CellStyle totalStyle = CellStyles.createTotalStyle(workbook);

        int rowIndex = 1; // Индекс первого ряда
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(org == null? "ПО БРЕНДАМ" : org);

        String headerSheet = org == null? "По брендам. Отгрузки" + " " + periodToHeader : org + " Отгрузки " + periodToHeader;
        Row titleRow = sheet.createRow(rowIndex++);
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),titleRow.getRowNum(),1,7));
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue(headerSheet);
        titleCell.setCellStyle(headerStyle);

        Row separatorRow  = sheet.createRow(rowIndex++);
        separatorRow.setHeightInPoints(5f);

        Row rowColumnHeader = sheet.createRow(rowIndex++);
        int arrayHeaderLength = org == null ? headersColumn.length - 1 : headersColumn.length;
        for(int i = 0; i < arrayHeaderLength; i++) {
            Cell cell = rowColumnHeader.createCell(i + 1);
            cell.setCellValue(" " + headersColumn[i] + " ");
            if (org == null && i == arrayHeaderLength - 1) {
                cell.setCellValue("Всего слэбов");
            }
            cell.setCellStyle(subHeaderStyle);
        }

        Map<String, Float[]> mapDataPerMonth = getMapDataPerMonth(listReportDTO,headersColumn,org);

        for(Map.Entry<String, Float[]> entry: mapDataPerMonth.entrySet()) {
            boolean isTotalRow = entry.getKey().equals("Итого");

            Row dataRow = sheet.createRow(rowIndex ++);
            Cell monthCell = dataRow.createCell(1);
            monthCell.setCellValue(entry.getKey());

            if(isTotalRow) {
                monthCell.setCellStyle(totalStyle);
            } else {
                monthCell.setCellStyle(cellStyle);
            }
            Float[] data = entry.getValue();
            for(int i = 0; i < data.length; i++) {
                Cell dataCell = dataRow.createCell(i + 2);
                dataCell.setCellValue(data[i] != null ? data[i] : 0);
                if (org == null && i == data.length -1) {
                    dataCell.setCellValue("");
                }
                if(isTotalRow ) {
                    if(org == null && i == data.length - 1) continue;
                    dataCell.setCellStyle(totalStyle);
                } else {
                    dataCell.setCellStyle(cellStyle);
                }
            }
        }
        // Автоматическое выравнивание колонок
        for (int i = 1; i <= headersColumn.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Устанавливаю ширину колонок
        sheet.setColumnWidth(0, 2 * 256);
        sheet.setColumnWidth(1, 11 * 256);
        sheet.setColumnWidth(headersColumn.length - 1, 14 * 256);
        sheet.setColumnWidth(headersColumn.length , 11 * 256);
    }

    private Map<String, Float[]> getMapDataPerMonthWithSum(List<SalesReportDTO> listReportDTO,String[] headersColumn, String organization) {

        Map<String, Float[]> mapDataPerMonth = new LinkedHashMap<>();

        Float[] dataTotal = new Float[headersCategoryColumn.length * 2 + 3];
        Arrays.fill(dataTotal,0.0f);

        float total = 0;
        float totalSumPerOrg = 0;
        float totalPerOrg = 0;

        for(SalesReportDTO salesReportDTO: listReportDTO) {

            String categoryName = salesReportDTO.getCategoryName();
            boolean contain = Arrays.asList(headersColumn).contains(categoryName);
            if(!contain) continue;

            float count = salesReportDTO.getTotalQuantity();
            float sum = salesReportDTO.getTotalSum() / 100;

            Month month = salesReportDTO.getMonth().getMonth();
            String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));

            Float[] dataPerMonth;
            if(!mapDataPerMonth.containsKey(monthName)) {
                dataPerMonth = new Float[headersColumn.length * 2 + 3];
                Arrays.fill(dataPerMonth, 0.0f);
                dataPerMonth[dataPerMonth.length - 1] = count;
            } else {
                dataPerMonth = mapDataPerMonth.get(monthName);
                dataPerMonth[dataPerMonth.length - 1] = dataPerMonth[dataPerMonth.length - 1] + count;
            }
            total = total + count;

            if(!salesReportDTO.getOrganizationName().equals(organization)) {
                mapDataPerMonth.put(monthName,dataPerMonth);
                continue;
            }


            dataPerMonth[dataPerMonth.length - 2] = dataPerMonth[dataPerMonth.length - 2] + sum;
            dataPerMonth[dataPerMonth.length - 3] = dataPerMonth[dataPerMonth.length - 3] + count;
            totalPerOrg = totalPerOrg + count;
            totalSumPerOrg = totalSumPerOrg + sum;

            int indexData = -1;
            for (int i = 0; i < headersColumn.length; i++) {
                if (headersColumn[i].equals(categoryName)) {
                    indexData = i == 0 ? i : i * 2;
                    dataTotal[indexData] = dataTotal[indexData] + count;
                    dataTotal[indexData + 1] = dataTotal[indexData + 1] + sum;
                    break;
                }
            }

            if(indexData != -1 ) {
                dataPerMonth[indexData] = count;
                dataPerMonth[indexData + 1] = sum;
            }
            mapDataPerMonth.put(monthName,dataPerMonth);
        }
        dataTotal[dataTotal.length - 1] = total;
        dataTotal[dataTotal.length - 2] = totalSumPerOrg;
        dataTotal[dataTotal.length - 3] = totalPerOrg;
        mapDataPerMonth.put("Итого", dataTotal);

        return mapDataPerMonth;
    }

    private Map<String, Float[]> getMapDataPerMonth(List<SalesReportDTO> listReportDTO,String[] headersColumn, String organization) {

        Map<String, Float[]> mapDataPerMonth = new LinkedHashMap<>();

        Float[] dataTotal = new Float[headersColumn.length - 1];
        Arrays.fill(dataTotal,0.0f);

        float total = 0;
        float totalPerOrg = 0;

        for(SalesReportDTO salesReportDTO: listReportDTO) {
            float quantity = salesReportDTO.getTotalQuantity();
            String categoryName = salesReportDTO.getCategoryName();
            boolean contain = Arrays.asList(headersColumn).contains(categoryName);
            if(!contain) continue;

            Month month = salesReportDTO.getMonth().getMonth();
            String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));

            Float[] dataPerMonth;
            if(!mapDataPerMonth.containsKey(monthName)) {
                dataPerMonth = new Float[headersColumn.length - 1];
                Arrays.fill(dataPerMonth, 0.0f);
                dataPerMonth[dataPerMonth.length - 1] = quantity;
            } else {
                dataPerMonth = mapDataPerMonth.get(monthName);
                dataPerMonth[dataPerMonth.length - 1] = dataPerMonth[dataPerMonth.length - 1] + quantity;
            }
            total = total + quantity;

            // если организация не передана - то цикл заново
            if(organization != null && !salesReportDTO.getOrganizationName().equals(organization)) {
                mapDataPerMonth.put(monthName,dataPerMonth);
                continue;
            }

            dataPerMonth[dataPerMonth.length - 2] = dataPerMonth[dataPerMonth.length - 2] + quantity;
            totalPerOrg = totalPerOrg + quantity;

            int indexData = -1;
            for (int i = 1; i < headersColumn.length; i++) {
                if (headersColumn[i].equals(categoryName)) {
                    indexData = i - 1;
                    dataTotal[indexData] = dataTotal[indexData] + quantity;
                    break;
                }
            }
            if(indexData != -1 && organization != null) dataPerMonth[indexData] = quantity;
            if(indexData != -1 && organization == null) dataPerMonth[indexData] = dataPerMonth[indexData] + quantity;
            mapDataPerMonth.put(monthName,dataPerMonth);
        }
        dataTotal[headersColumn.length - 2] = total;
        dataTotal[headersColumn.length - 3] = totalPerOrg;
        mapDataPerMonth.put("Итого", dataTotal);

        return mapDataPerMonth;
    }

    private String getPeriodAsString(List<SalesReportDTO> listReportDTO) {
        List<LocalDate> listMonth = listReportDTO
                .stream()
                .map(e -> e.getMonth().atStartOfDay().toLocalDate())// приводим к LocalDate (1-е число месяца, 00:00)
                .toList();
        String periodToHeader = "";
        if(!listMonth.isEmpty()) {
            LocalDate minDate = listMonth.get(0); // первая дата — начало диапазона
            LocalDate maxDate = listMonth.get(listMonth.size() - 1); // последняя дата — первый день последнего месяца
            LocalDate maxDateMaxDay = maxDate.with(TemporalAdjusters.lastDayOfMonth()); // устанавливаю последний день месяца
            periodToHeader =
                    "c 1 " + minDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("ru")) +
                    " по " + maxDateMaxDay.getDayOfMonth() + " " +
                    maxDateMaxDay.getMonth().getDisplayName(TextStyle.FULL, new Locale("ru")) +
                    " " + maxDateMaxDay.getYear() + "г.";
        }
        return periodToHeader;
    }







}
