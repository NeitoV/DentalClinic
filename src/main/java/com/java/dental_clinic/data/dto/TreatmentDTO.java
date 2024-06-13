package com.java.dental_clinic.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentDTO {
    private Long id;
    private int cost;
    private String name;
    private String note;
    private ServiceDTO serviceDTO;
}
