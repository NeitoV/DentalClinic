package com.java.dental_clinic.data.dto.procedure;

import com.java.dental_clinic.data.dto.TreatmentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcedureShowDTO {
    private Long id;
    private TreatmentDTO treatmentDTO;
    private int count;
    private String note;
}
