package com.java.dental_clinic.data.dto.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private Long id;
    private String name;
    private String birthday;
    private String address;
    private String medicalHistory;
    private String phoneNumber;
}
