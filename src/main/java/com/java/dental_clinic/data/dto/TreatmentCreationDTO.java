package com.java.dental_clinic.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TreatmentCreationDTO {
    private int cost;
    private String name;
    private String note;
    private String unit;
    private Long serviceId;
}
