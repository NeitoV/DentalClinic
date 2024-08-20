package com.java.dental_clinic.data.dto;

import com.java.dental_clinic.data.dto.patient.PatientDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {
    private Long id;
    private String note;
    private LocalDateTime date;
    private boolean isConfirm;
    private WorkingShowDTO workingShowDTO;
    private PatientDTO patientDTO;
}
