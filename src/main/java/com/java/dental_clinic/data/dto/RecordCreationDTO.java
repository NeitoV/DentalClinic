package com.java.dental_clinic.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordCreationDTO {
    private String diagnosis;
    private String note;
    private Long patientId;
    private List<ProcedureCreationDTO> procedureCreationDTOS;
}
