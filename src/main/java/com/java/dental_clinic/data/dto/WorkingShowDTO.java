package com.java.dental_clinic.data.dto;

import com.java.dental_clinic.data.dto.patient.PatientDTO;
import com.java.dental_clinic.data.dto.staff.StaffDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkingShowDTO {
    private Long id;
    private WorkingDTO workingDTO;
    private StaffDTO staffDTO;
}
