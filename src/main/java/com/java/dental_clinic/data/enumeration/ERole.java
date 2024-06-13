package com.java.dental_clinic.data.enumeration;

public enum ERole {
    ADMIN("Role_Admin"),
    STAFF("Role_Staff"),
    PATIENT("Role_Patient");

    public static long roleAdmin = 1;
    public static long roleStaff = 2;
    public static long rolePatient = 3;

    private final String name;

    ERole(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
