package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.*;
import com.java.dental_clinic.data.entity.MedicalRecord;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MedicalRecordService {
    @Transactional
    MessageResponse createMedicalRecord(RecordCreationDTO recordCreationDTO);

    PaginationDTO findByPatientId(Long patientId, int pageNumber, int pageSize);

    @Transactional
    MessageResponse deleteRecord(Long recordId);

    MessageResponse updateStatusDone(Long recordId);

    MessageResponse reOpenMedicalRecord(Long recordId);

    void createListObjectives(List<ObjectivesCreationDTO> objectivesCreationDTOS, MedicalRecord record);

    PaginationDTO findByToken(int pageNumber, int pageSize);
}
