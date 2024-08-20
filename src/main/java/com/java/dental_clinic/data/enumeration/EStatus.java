package com.java.dental_clinic.data.enumeration;

public enum EStatus {
    ONGOING("Ongoing"),
    DONE("Done");

    private final String name;

    EStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
