package com.java.dental_clinic.data.dto.patient;

import com.java.dental_clinic.data.dto.LoginDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientCreationDTO {
    private PatientDTO patientDTO;
    private LoginDTO loginDTO;
}
