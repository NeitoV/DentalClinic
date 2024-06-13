package com.java.dental_clinic.data.enumeration;

public enum EPosition {
    DENTIST("dentist"),
    RECEPTIONIST("receptionist");

    public static long positionDentist = 1;
    public static long positionReceptionist = 2;

    private final String name;
    EPosition(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
