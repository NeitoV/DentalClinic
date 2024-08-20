package com.java.dental_clinic.data.dto.procedure;

import com.java.dental_clinic.data.dto.ObjectiveShowDTO;
import com.java.dental_clinic.data.dto.RecordShowDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcedureRecordShowDTO {
    private RecordShowDTO recordShowDTO;
    private List<ObjectiveShowDTO> objectiveShowDTOS;
}
