package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ProcedureCreationDTO;
import com.java.dental_clinic.data.dto.ProcedureRecordShowDTO;
import com.java.dental_clinic.data.dto.ProcedureShowDTO;
import com.java.dental_clinic.data.entity.MedicalRecord;
import com.java.dental_clinic.data.entity.TherapyProcedure;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TherapyProcedureService {
    ProcedureRecordShowDTO findByMedicalRecordId(Long recordId);

    @Transactional
    MessageResponse createTherapyProcedure(Long recordId, List<ProcedureCreationDTO> list);

    List<TherapyProcedure> mapCreationProcedure(MedicalRecord medicalRecord, List<ProcedureCreationDTO> list);

    MessageResponse deleteTherapyProcedure(Long id);
}
