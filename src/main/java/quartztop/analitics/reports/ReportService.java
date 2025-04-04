package quartztop.analitics.reports;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;
import quartztop.analitics.dtos.counterparty.AgentDTO;
import quartztop.analitics.dtos.counterparty.GroupAgentDTO;
import quartztop.analitics.dtos.organizationData.OwnerDTO;
import quartztop.analitics.dtos.products.CategoryDTO;
import quartztop.analitics.models.counterparty.AgentEntity;
import quartztop.analitics.models.counterparty.GroupAgentEntity;
import quartztop.analitics.models.products.CategoryEntity;
import quartztop.analitics.services.counterparty.AgentCRUDService;
import quartztop.analitics.services.counterparty.GroupAgentCRUDService;
import quartztop.analitics.services.crudDemandPositions.DemandPositionCRUDService;
import quartztop.analitics.services.crudOrganization.OwnerCRUDService;
import quartztop.analitics.services.crudProduct.CategoryCRUDService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final AgentCRUDService agentCRUDService;
    private final GroupAgentCRUDService groupAgentCRUDService;
    private final DemandPositionCRUDService demandPositionCRUDService;
    private final OwnerCRUDService ownerCRUDService;

    private final short indent = 1;

    public GeneralReportsDTO createReportByGroupAgentsAndCategories( UUID managerId, List<Integer> listIdTags,
                                                        LocalDate startPeriod, LocalDate endPeriod,
                                                        LocalDate comparisonPeriodStart, LocalDate comparisonPeriodEnd)
    {

        LocalDateTime start = startPeriod.atStartOfDay();
        LocalDateTime end = endPeriod.atTime(23,59,59);
        LocalDateTime startCompare = comparisonPeriodStart.atStartOfDay();
        LocalDateTime endCompare = comparisonPeriodEnd.atTime(23,59,59);
        OwnerDTO ownerDTO = ownerCRUDService.getOwnerDto(managerId);

        GeneralReportsDTO generalReportsDTO = new GeneralReportsDTO();


        generalReportsDTO.setStartPeriod(start);
        generalReportsDTO.setEndPeriod(end);
        generalReportsDTO.setStartComparePeriod(startCompare);
        generalReportsDTO.setEndComparePeriod(endCompare);
        generalReportsDTO.setOwnerDTO(ownerDTO);

        // ПОЛУЧАЮ ГРУППЫ
        List<GroupAgentEntity> groupAgentEntityList = listIdTags
                .stream()
                .map(groupAgentCRUDService::getEntity)
                .toList();

        // ПОЛУЧАЮ ПРОДАЖИ ПО ГРУППАМ И ПЕРИОДУ ВРЕМЕНИ
        for(GroupAgentEntity groupAgentEntity: groupAgentEntityList) {

            GroupAgentDTO groupAgentDTO = GroupAgentCRUDService.mapToDTO(groupAgentEntity);

            ReportByGroupAgentDTO reportByGroupAgentDTO = new ReportByGroupAgentDTO();
            reportByGroupAgentDTO.setGroupAgentDTO(groupAgentDTO);

            List<AgentEntity> listUniqueAgentByTag = agentCRUDService.getListEntityByTag(groupAgentEntity);

            for(AgentEntity agent: listUniqueAgentByTag) {

                // ПОЛУЧАЮ СET  КАТЕГОРИЙ
                List<CategoryEntity> categoryEntities = demandPositionCRUDService.getListUniqueCategoryByPeriodAndAgent(start,end,agent);
                List<CategoryEntity> comparePeriodCategoryEntities = demandPositionCRUDService.getListUniqueCategoryByPeriodAndAgent(startCompare,endCompare,agent);

                Set<CategoryEntity> setUniqueCategoryEntity = new HashSet<>(categoryEntities);
                setUniqueCategoryEntity.addAll(comparePeriodCategoryEntities);

                ReportByAgentsDTO reportByAgentsDTO = new ReportByAgentsDTO();

                AgentDTO agentDTO = AgentCRUDService.mapToDTO(agent);
                reportByAgentsDTO.setAgentDTO(agentDTO);

                for(CategoryEntity categoryEntity: setUniqueCategoryEntity) {
                    if (categoryEntity.getName().equals("Готовые изделия")) continue;

                    double count = demandPositionCRUDService.getCountProductByPeriodAndAgentAndCategory(start,end,categoryEntity,agent);
                    double comparePeriodCount = demandPositionCRUDService.getCountProductByPeriodAndAgentAndCategory(startCompare,endCompare,categoryEntity,agent);

                    double sum = demandPositionCRUDService.getSumPriceByPeriodAndAgentAndCategory(start,end,categoryEntity,agent);
                    double sumCompare = demandPositionCRUDService.getSumPriceByPeriodAndAgentAndCategory(startCompare,endCompare,categoryEntity,agent);
                    if(count == 0 && comparePeriodCount == 0) continue;
                    reportByAgentsDTO.setCountCurrentPeriod(reportByAgentsDTO.getCountCurrentPeriod() + count);
                    reportByAgentsDTO.setCountComparePeriod(reportByAgentsDTO.getCountComparePeriod() + comparePeriodCount);
                    reportByAgentsDTO.setSumCurrentPeriod(reportByAgentsDTO.getSumCurrentPeriod() + sum);
                    reportByAgentsDTO.setSumComparePeriod(reportByAgentsDTO.getSumComparePeriod() + sumCompare);

                    CategoryDTO categoryDTO = CategoryCRUDService.mapToDto(categoryEntity);
                    ReportsByCategoryDTO reportsByCategoryDTO = new ReportsByCategoryDTO();
                    reportsByCategoryDTO.setCategoryDTO(categoryDTO);
                    reportsByCategoryDTO.setCurrentPeriodCount(count);
                    reportsByCategoryDTO.setComparePeriodCount(comparePeriodCount);
                    reportsByCategoryDTO.setCurrentPeriodSum(sum);
                    reportsByCategoryDTO.setComparePeriodSum(sumCompare);

                    reportByAgentsDTO.getReportsByCategoryList().add(reportsByCategoryDTO);
                    reportByGroupAgentDTO.getMapDataCategory().merge(
                            categoryDTO,
                            new Double[]{count,sum, comparePeriodCount, sumCompare},
                            (oldVal, newVal) -> new Double[]{
                                    oldVal[0] + newVal[0],
                                    oldVal[1] + newVal[1],
                                    oldVal[2] + newVal[2],
                                    oldVal[3] + newVal[3]
                            }
                    );
                }
                reportByGroupAgentDTO.getReportByAgentsList().add(reportByAgentsDTO);
                reportByGroupAgentDTO.setCountCurrentPeriod(reportByGroupAgentDTO.getCountCurrentPeriod() + reportByAgentsDTO.getCountCurrentPeriod());
                reportByGroupAgentDTO.setCountComparePeriod(reportByGroupAgentDTO.getCountComparePeriod() + reportByAgentsDTO.getCountComparePeriod());
                reportByGroupAgentDTO.setSumCurrentPeriod(reportByGroupAgentDTO.getSumCurrentPeriod() + reportByAgentsDTO.getSumCurrentPeriod());
                reportByGroupAgentDTO.setSumComparePeriod(reportByGroupAgentDTO.getSumComparePeriod() + reportByAgentsDTO.getSumComparePeriod());


            }
            generalReportsDTO.getReportByGroupAgentDTOList().add(reportByGroupAgentDTO);
            generalReportsDTO.setTotalCurrentCount(generalReportsDTO.getTotalCurrentCount() + reportByGroupAgentDTO.getCountCurrentPeriod());
            generalReportsDTO.setTotalCompareCount(generalReportsDTO.getTotalCompareCount() + reportByGroupAgentDTO.getCountComparePeriod());

        }
        //createExcelSheetReportOrders(generalReportsDTO);
        return generalReportsDTO;
    }

    public Workbook createExcelSheetReportOrders(GeneralReportsDTO reportsDTO) {

        Workbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("report");
        // Создаем стиль для ячеек
        CellStyle headerStyle = createHeaderCellStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);
        CellStyle subHeaderStyle = createSubHeaderCellStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        // Создаем заголовки
        Row titleRow = sheet.createRow(1);
        LocalDate startLocalDate = reportsDTO.getStartPeriod().toLocalDate();
        LocalDate endLocalDate = reportsDTO.getEndPeriod().toLocalDate();
        LocalDate startCompareLocalDate = reportsDTO.getStartComparePeriod().toLocalDate();
        LocalDate endCompareLocalDate = reportsDTO.getEndComparePeriod().toLocalDate();
        String header = "Менеджер: " + reportsDTO.getOwnerDTO().getName() + "  " +  startLocalDate +
                " - " + endLocalDate + " <---> " + startCompareLocalDate + " - " + endCompareLocalDate +
                "  Всего отгружено товара за период: " + reportsDTO.getTotalCurrentCount() +
                " за период сравнения: " + reportsDTO.getTotalCompareCount() ;

        Cell headerCell = titleRow.createCell(1);
        headerCell.setCellValue(header);
        headerCell.setCellStyle(headerStyle);

        // Создаем заголовки
        var headerRow = sheet.createRow(2);

        String[] headers = {"Группа контрагентов  ", "Группы Товаров  ", "Количество  ", "Стоимость  ", "  ",
                "Количество  ", "Стоимость  "};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i + 1);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(subHeaderStyle);
        }

        List<ReportByGroupAgentDTO> reportByGroupAgentDTOList = reportsDTO.getReportByGroupAgentDTOList();
        reportByGroupAgentDTOList.forEach(e -> addData(workbook,e));

        // Автоматическое выравнивание колонок
        for (int i = 1; i <= headers.length; i++) {
            sheet.autoSizeColumn(i);
        }


        //saveWorkbook(workbook);
        return workbook;
    }

    private void addData(Workbook workbook, ReportByGroupAgentDTO reportByGroupAgentDTO) {

        CellStyle numberStyle = createNumberStyle(workbook);

        // Заполняем данные
        Sheet sheet = workbook.getSheetAt(0); // Получаем первый лист
        int lastRowIndex = sheet.getLastRowNum(); // Индекс последней строки
        int rowNum = lastRowIndex;
        rowNum++;
        int startRow = rowNum;
        Row titleGroupRow = sheet.createRow(rowNum);
        titleGroupRow.createCell(1).setCellValue(reportByGroupAgentDTO.getGroupAgentDTO().getTag());
        titleGroupRow.createCell(2);
        titleGroupRow.createCell(3).setCellValue(reportByGroupAgentDTO.getCountCurrentPeriod());
        titleGroupRow.createCell(4).setCellValue(reportByGroupAgentDTO.getSumCurrentPeriod());
        titleGroupRow.createCell(5);
        titleGroupRow.createCell(6).setCellValue(reportByGroupAgentDTO.getCountComparePeriod());
        titleGroupRow.createCell(7).setCellValue(reportByGroupAgentDTO.getSumComparePeriod());

        rowNum++;

        Map<CategoryDTO, Double[]> categoriesDataMap = reportByGroupAgentDTO.getMapDataCategory();
        for (Map.Entry<CategoryDTO, Double[]> map: categoriesDataMap.entrySet()) {
            Row categoryDataRow = sheet.createRow(rowNum++);

            categoryDataRow.createCell(2).setCellValue(map.getKey().getName());
            categoryDataRow.createCell(3).setCellValue(map.getValue()[0]);
            Cell cdCell4 = categoryDataRow.createCell(4);
            cdCell4.setCellValue(map.getValue()[1]);
            cdCell4.setCellStyle(numberStyle);
            categoryDataRow.createCell(6).setCellValue(map.getValue()[2]);
            Cell cdCell7 = categoryDataRow.createCell(7);
            cdCell7.setCellValue(map.getValue()[3]);
            cdCell7.setCellStyle(numberStyle);
        }


        for (int i = 0; i < 8; i++) {
            Cell cell = titleGroupRow.getCell(i); // Получаем существующую ячейку
            if (cell != null) {
                cell.setCellStyle(createTitleGroupCellStyle(workbook));
            }
        }

        rowNum++;

        List<ReportByAgentsDTO> listReportsByAgent = reportByGroupAgentDTO.getReportByAgentsList();
        for(int i = 0; i < listReportsByAgent.size(); i++) {
            ReportByAgentsDTO reportByAgentsDTO = listReportsByAgent.get(i);
            // Запоминаем, с какой строки начинается отчет по контрагенту
            int startAgentRow = rowNum;
            Row agentRow = sheet.createRow(rowNum++);

            agentRow.createCell(1).setCellValue(reportByAgentsDTO.getAgentDTO().getName());

            agentRow.createCell(3).setCellValue(reportByAgentsDTO.getCountCurrentPeriod());
            //agentRow.createCell(4).setCellValue(reportByAgentsDTO.getSumCurrentPeriod());
            Cell agCell4 = agentRow.createCell(4);
            agCell4.setCellValue(reportByAgentsDTO.getSumCurrentPeriod());
            agCell4.setCellStyle(numberStyle);
            agentRow.createCell(6).setCellValue(reportByAgentsDTO.getCountComparePeriod());
            //agentRow.createCell(7).setCellValue(reportByAgentsDTO.getSumComparePeriod());
            Cell agCell7 = agentRow.createCell(7);
            agCell7.setCellValue(reportByAgentsDTO.getSumComparePeriod());
            agCell7.setCellStyle(numberStyle);

            List<ReportsByCategoryDTO> reportsByCategoryDTOList = reportByAgentsDTO.getReportsByCategoryList();

            for(ReportsByCategoryDTO reportsByCategoryDTO : reportsByCategoryDTOList) {

                Row categoryRow = sheet.createRow(rowNum++);
                categoryRow.createCell(2).setCellValue(reportsByCategoryDTO.getCategoryDTO().getName());
                categoryRow.createCell(3).setCellValue(reportsByCategoryDTO.getCurrentPeriodCount());
                Cell cell4 = categoryRow.createCell(4);
                cell4.setCellValue(reportsByCategoryDTO.getCurrentPeriodSum());
                cell4.setCellStyle(numberStyle);
                //categoryRow.createCell(7).setCellValue(reportsByCategoryDTO.getCategoryDTO().getName());
                categoryRow.createCell(6).setCellValue(reportsByCategoryDTO.getComparePeriodCount());
                Cell cell7 = categoryRow.createCell(7);
                cell7.setCellStyle(numberStyle);
                cell7.setCellValue(reportsByCategoryDTO.getComparePeriodSum());

            }
            // Группируем строки (все категории привязываются к агенту)
            if (rowNum > startAgentRow) {
                sheet.groupRow(startAgentRow + 1, rowNum + 1); // Группируем строки
                sheet.setRowGroupCollapsed(startAgentRow + 1, true); // Скрываем по умолчанию
            }
            rowNum++;
        }

        // Группируем строки (все контрагенты привязываются к группе)
        if (rowNum > startRow) {
            sheet.groupRow(startRow + categoriesDataMap.size() + 1, rowNum + 1); // Группируем строки
            sheet.setRowGroupCollapsed(startRow + categoriesDataMap.size() + 1, true); // Скрываем по умолчанию

        }
    }

    private void saveWorkbook(Workbook workbook) {
        String folderPath = Paths.get("src", "main", "resources", "reports").toString();
        File directory = new File(folderPath);

        if (!directory.exists()) {
            directory.mkdirs(); // Создаём папку, если её нет
        }

        String filePath = Paths.get(folderPath, "report.xlsx").toString();

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            workbook.close(); // Закрываем, чтобы освободить ресурсы
            System.out.println("Файл успешно сохранен: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CellStyle createCellStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        style.setIndention(indent); // Устанавливаем уровень отступа
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание
        style.setAlignment(HorizontalAlignment.LEFT); // Горизонтальное выравнивание (по умолчанию)
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        // Устанавливаем формат с разделителем тысяч и двумя знаками после запятой
        style.setDataFormat(format.getFormat("#,##0.00"));
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание
        style.setAlignment(HorizontalAlignment.RIGHT); // Горизонтальное выравнивание (по умолчанию)
        return style;
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // Устанавливаем нижнюю границу
        //style.setBorderBottom(BorderStyle.THIN); // Тип границы (например, тонкая линия)
        // Создаем шрифт
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE); // Подчеркивание (одиночное)
        font.setItalic(true); // Устанавливаем курсивный шрифт
        font.setBold(true); // Устанавливаем жирный шрифт
        style.setIndention(indent); // Устанавливаем уровень отступа
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание
        // Применяем шрифт к стилю
        style.setFont(font);

        // Дополнительные настройки выравнивания
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }
    private CellStyle createTitleGroupCellStyle(Workbook workbook) {
        XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Создаём XSSFColor через массив байтов RGB
        byte[] rgb = new byte[]{(byte) 189, (byte) 215, (byte) 238}; // RGB для голубого цвета
        XSSFColor backgroundColour = new XSSFColor(rgb, new DefaultIndexedColorMap());
        cellStyle.setFillForegroundColor(backgroundColour);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(true); // Делаем шрифт жирным
        // Применяем шрифт к стилю
        cellStyle.setFont(font);

        DataFormat format = workbook.createDataFormat();
        // Устанавливаем формат с разделителем тысяч и двумя знаками после запятой
        cellStyle.setDataFormat(format.getFormat("#,##0.00"));

        return cellStyle;
    }

    private CellStyle createSubHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // Цвет фона
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // Тип заливки
        // Устанавливаем нижнюю границу
        //style.setBorderBottom(BorderStyle.THIN); // Тип границы (например, тонкая линия)
        // Создаем шрифт
        Font font = workbook.createFont();
        //font.setItalic(true); // Устанавливаем курсивный шрифт
        font.setBold(true); // Устанавливаем жирный шрифт
        style.setIndention(indent); // Устанавливаем уровень отступа
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание
        // Применяем шрифт к стилю
        style.setFont(font);

        // Дополнительные настройки выравнивания
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

}
