package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ObjectivesCreationDTO;
import com.java.dental_clinic.data.dto.RecordCreationDTO;
import com.java.dental_clinic.data.dto.RecordShowDTO;
import com.java.dental_clinic.data.entity.MedicalRecord;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MedicalRecordService {
    @Transactional
    MessageResponse createMedicalRecord(RecordCreationDTO recordCreationDTO);

    List<RecordShowDTO> findByPatientId(Long patientId);

    @Transactional
    MessageResponse deleteRecord(Long recordId);

    MessageResponse updateStatusDone(Long recordId);

    MessageResponse reOpenMedicalRecord(Long recordId);

    void createListObjectives(List<ObjectivesCreationDTO> objectivesCreationDTOS, MedicalRecord record);

    List<RecordShowDTO> findByToken();
}
