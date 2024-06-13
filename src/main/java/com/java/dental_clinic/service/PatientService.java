package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.PaginationDTO;
import com.java.dental_clinic.data.dto.patient.PatientCreationDTO;
import com.java.dental_clinic.data.dto.patient.PatientDTO;
import com.java.dental_clinic.data.entity.Patient;

public interface PatientService {
    MessageResponse createPatient(PatientCreationDTO patientCreationDTO);

    MessageResponse updatePatient(PatientDTO patientDTO, Long id);

    PaginationDTO filterPatient(String keyword, int pageNumber, int pageSize);

    Patient getPatientByToken();
}
