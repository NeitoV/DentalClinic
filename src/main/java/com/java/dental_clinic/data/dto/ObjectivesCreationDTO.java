package com.java.dental_clinic.data.dto;

import com.java.dental_clinic.data.dto.procedure.ProcedureCreationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectivesCreationDTO {
    private String diagnosis;
    private String note;
    private List<ProcedureCreationDTO> procedureCreationDTOList;
}
