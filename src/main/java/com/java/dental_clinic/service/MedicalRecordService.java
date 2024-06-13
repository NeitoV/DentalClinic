package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ProcedureShowDTO;
import com.java.dental_clinic.data.dto.RecordCreationDTO;
import com.java.dental_clinic.data.dto.RecordShowDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MedicalRecordService {
    @Transactional
    MessageResponse createMedicalRecord(RecordCreationDTO recordCreationDTO);

    List<RecordShowDTO> findByPatientId(Long patientId);
}
