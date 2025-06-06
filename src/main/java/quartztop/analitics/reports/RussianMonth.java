package quartztop.analitics.reports;

import java.time.Month;

public enum RussianMonth {
    JANUARY("январь"),
    FEBRUARY("февраль"),
    MARCH("март"),
    APRIL("апрель"),
    MAY("май"),
    JUNE("июнь"),
    JULY("июль"),
    AUGUST("август"),
    SEPTEMBER("сентябрь"),
    OCTOBER("октябрь"),
    NOVEMBER("ноябрь"),
    DECEMBER("декабрь");

    private final String nominative;

    RussianMonth(String nominative) {
        this.nominative = nominative;
    }

    public String getNominative() {
        return nominative;
    }

    public static String of(Month month) {
        return values()[month.getValue() - 1].getNominative();
    }
}

