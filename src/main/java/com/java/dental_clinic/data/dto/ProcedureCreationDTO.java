package com.java.dental_clinic.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcedureCreationDTO {
    private Long treatmentId;
    private String note;
    private int count;
}
