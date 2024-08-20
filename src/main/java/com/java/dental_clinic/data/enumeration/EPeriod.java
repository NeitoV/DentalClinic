package com.java.dental_clinic.data.enumeration;

public enum EPeriod {
    MORNING("morning"),
    AFTERNOON("afternoon");

    public static long periodMorning = 1;
    public static long periodAfternoon = 2;

    private final String name;
    EPeriod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
