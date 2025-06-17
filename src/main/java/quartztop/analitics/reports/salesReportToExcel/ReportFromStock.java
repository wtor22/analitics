package quartztop.analitics.reports.salesReportToExcel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import quartztop.analitics.repositories.reports.ReportsStockByStoreRepository;
import quartztop.analitics.services.reports.ReportStockByStoreService;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportFromStock {

    private final ReportStockByStoreService reportStockByStoreService;
    private final ReportsStockByStoreRepository reportsStockByStoreRepository;

    private String[] storeToWrite;
    private final String[] categoriesNames = {"Belenco","Casablanca (Въетнам)","CALISCO (Турция)",
            "Strong Quartz (Китай)","Coante","Italstone","Materia","Fondovalle","Ascale","GUIDONI (Испания)",
            "SHANGHAI CSC NEW MATERIAL"};
    private String[] storeNames = {"Склад Минск","Склад Балашиха","СПБ Петробрас","Склад Краснодар"};

    private final String[] rbStoreNames = {"Склад Минск"};
    private final String[] ruStoreNames = {"Склад Балашиха","СПБ Петробрас","Склад Краснодар"};


    public List<StockByStoreAndCategoryDTO> getListDto() {
        return reportsStockByStoreRepository.getStockReportsGroupByStoreAndCategory();

    }
    public Workbook createExcelBookReportStockProducts() {

        List<StockByStoreAndCategoryDTO> list = reportsStockByStoreRepository.getStockReportsGroupByStoreAndCategory();

        Workbook workbook = new XSSFWorkbook();

        // Создаем стили для ячеек
        CellStyle headerStyle = CellStyles.createHeaderCellStyle(workbook);



        // Формирую дату отчета
        LocalDate dayReport = LocalDate.now();


        int rowIndex = 1; // Индекс первого ряда
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("Лист1");

        String headerSheet =  "Остатки товаров на складах на " + dayReport;

        Row titleRow = sheet.createRow(rowIndex++);
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),titleRow.getRowNum(),1,7));
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue(headerSheet);
        titleCell.setCellStyle(headerStyle);


        rowIndex += 2;
        fillTableData(workbook,sheet, list, rowIndex, "РБ");
        fillTableData(workbook,sheet, list, rowIndex, "РФ");
        fillTableData(workbook,sheet, list, rowIndex, "Всего по всем складам");

        return workbook;
    }

    private void fillTableData(Workbook workbook, XSSFSheet sheet, List<StockByStoreAndCategoryDTO> list, int rowIndex, String country) {

        switch (country) {
            case "РБ" -> storeToWrite = rbStoreNames;
            case "РФ" -> storeToWrite = ruStoreNames;
            default -> storeToWrite = storeNames;
        }

        CellStyle subHeaderStyle = CellStyles.createSubHeaderCellStyle(workbook);
        CellStyle totalStyle = CellStyles.createTotalStyle(workbook);
        CellStyle headerStyle = CellStyles.createHeaderCellStyle(workbook);
        CellStyle cellStyle = CellStyles.createCellStyle(workbook);

        rowIndex = sheet.getLastRowNum() + 3;

        Row countryHeaderRow = sheet.createRow(rowIndex++);
        Cell countryCell = countryHeaderRow.createCell(1);
        countryCell.setCellValue(country);
        countryCell.setCellStyle(headerStyle);

        Row headerColumn = sheet.createRow(rowIndex++);
        Cell firstTableCell = headerColumn.createCell(1);
        firstTableCell.setCellValue("Склад / Категория");
        firstTableCell.setCellStyle(subHeaderStyle);

        for(int i = 0; i < categoriesNames.length; i++) {
            Cell headerCell = headerColumn.createCell(i + 2);
            headerCell.setCellValue(categoriesNames[i]);
            headerCell.setCellStyle(subHeaderStyle);
        }

        Map<String, Float[]> mapData = getMapData(list);

        if (country.equals("Всего по всем складам")) {
            Row dataRow = sheet.createRow(rowIndex ++);
            Cell storeNameCell = dataRow.createCell(1);
            storeNameCell.setCellValue("Итого");
            storeNameCell.setCellStyle(totalStyle);

            Float[] arrayTotalData = mapData.get("Итого");

            for(int i = 0; i < arrayTotalData.length; i++) {
                float quantity = arrayTotalData[i] != null ? arrayTotalData[i] : 0;
                Cell dataCell = dataRow.createCell(i + 2);
                dataCell.setCellValue(quantity);
                dataCell.setCellStyle(totalStyle);
            }
            return;
        }


        for(Map.Entry<String, Float[]> entry: mapData.entrySet()) {

            boolean isTotalRow = entry.getKey().equals("Итого");


            Row dataRow = sheet.createRow(rowIndex ++);
            Cell storeNameCell = dataRow.createCell(1);
            storeNameCell.setCellValue(entry.getKey());

            if(isTotalRow) {
                storeNameCell.setCellStyle(totalStyle);
            } else {
                storeNameCell.setCellStyle(cellStyle);
            }
            Float[] data = entry.getValue();

            for(int i = 0; i < data.length; i++) {

                float quantity = data[i] != null ? data[i] : 0;
                Cell dataCell = dataRow.createCell(i + 2);
                dataCell.setCellValue(quantity);

                if(isTotalRow) {
                    dataCell.setCellStyle(totalStyle);
                } else {
                    dataCell.setCellStyle(cellStyle);
                }
            }
        }

        sheet.setColumnWidth(0, 2 * 256);
        sheet.setColumnWidth(1, 17 * 256);
        for (int i = 2; i <= categoriesNames.length + 2; i++) {
            sheet.setColumnWidth(i, 15 * 256);
        }



    }

    private Map<String, Float[]> getMapData(List<StockByStoreAndCategoryDTO> listReportDTO) {
        Map<String, Float[]> mapData = new LinkedHashMap<>();

        // Предзаполним map для быстрого доступа: Map<storeName, Map<categoryName, stock>>
        Map<String, Map<String, Float>> fastLookup = new HashMap<>();
        for (StockByStoreAndCategoryDTO dto : listReportDTO) {
            fastLookup
                    .computeIfAbsent(dto.getStoreName(), k -> new HashMap<>())
                    .put(dto.getCategoryName(), dto.getStock());
        }

        //Для итогово ряда
        Float[] totalData = new Float[categoriesNames.length];
        Arrays.fill(totalData,0.0f);

        // Сбор финальной структуры
        for (String storeName : storeToWrite) {
            Float[] dataArray = new Float[categoriesNames.length];

            for (int c = 0; c < categoriesNames.length; c++) {
                String categoryName = categoriesNames[c];
                Float stock = fastLookup
                        .getOrDefault(storeName, Collections.emptyMap())
                        .getOrDefault(categoryName, 0f); // если нет – ставим 0

                dataArray[c] = stock;
                totalData[c] = totalData[c] + stock;
            }

            mapData.put(storeName, dataArray);
        }
        mapData.put("Итого",totalData);

        return mapData;
    }



}
