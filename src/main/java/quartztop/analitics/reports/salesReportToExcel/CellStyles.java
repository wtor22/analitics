package quartztop.analitics.reports.salesReportToExcel;

import org.apache.poi.ss.usermodel.*;

public class CellStyles {

    private final static short indent = 1;

    static CellStyle createTotalStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setBold(true); // Устанавливаем жирный шрифт
        style.setIndention(indent); // Устанавливаем уровень отступа
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание
        // Применяем шрифт к стилю
        style.setFont(font);
        return style;
    }

    // Дефолтный стиль для всех ячеек
    static CellStyle createCellStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        style.setIndention(indent); // Устанавливаем уровень отступа
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание

        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("0.00"));
        return style;
    }

    // Cтиль для ячеек с рейтингом
    static CellStyle createCellStyleRating(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    // Стиль для заголовка листа
    static CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();// Создаем шрифт
        font.setUnderline(Font.U_SINGLE); // Подчеркивание (одиночное)
        font.setItalic(true); // Устанавливаем курсивный шрифт
        font.setBold(true); // Устанавливаем жирный шрифт
        style.setIndention(indent); // Устанавливаем уровень отступа
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание
        style.setFont(font);// Применяем шрифт к стилю
        style.setVerticalAlignment(VerticalAlignment.CENTER);// Вертикальное выравнивание

        return style;
    }

    static CellStyle createSubHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // Цвет фона
        //style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // Тип заливки
        // Устанавливаем нижнюю границу
        style.setBorderBottom(BorderStyle.THIN); // Тип границы (например, тонкая линия)
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        // Создаем шрифт
        Font font = workbook.createFont();
        font.setItalic(true); // Устанавливаем курсивный шрифт
        font.setBold(true); // Устанавливаем жирный шрифт
        //style.setIndention(indent); // Устанавливаем уровень отступа
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);// Применяем шрифт к стилю
        style.setWrapText(true);
        return style;
    }
    static CellStyle createMonthSubHeader(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Устанавливаем границы
        style.setBorderBottom(BorderStyle.THIN); // Тип границы (например, тонкая линия)
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        // Создаем шрифт
        Font font = workbook.createFont();
        font.setItalic(true); // Устанавливаем курсивный шрифт
        font.setBold(true); // Устанавливаем жирный шрифт
        style.setIndention(indent); // Устанавливаем уровень отступа
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Вертикальное выравнивание
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font);// Применяем шрифт к стилю
        return style;
    }
}
