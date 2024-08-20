package com.java.dental_clinic.data.dto;

import com.java.dental_clinic.data.dto.procedure.ProcedureCreationDTO;
import com.java.dental_clinic.data.dto.procedure.ProcedureShowDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectiveShowDTO {
    private Long id;
    private String diagnosis;
    private LocalDate examinationDate;
    private String note;
    private List<ProcedureShowDTO> procedureShowDTOS;
}
