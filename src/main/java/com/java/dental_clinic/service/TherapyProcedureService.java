package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ObjectiveShowDTO;
import com.java.dental_clinic.data.dto.procedure.ProcedureCreationDTO;
import com.java.dental_clinic.data.dto.procedure.ProcedureRecordShowDTO;
import com.java.dental_clinic.data.dto.procedure.ProcedureShowDTO;
import com.java.dental_clinic.data.entity.Objective;
import com.java.dental_clinic.data.entity.TherapyProcedure;

import java.util.List;

public interface TherapyProcedureService {
    ProcedureRecordShowDTO findByMedicalRecordId(Long recordId);

    List<TherapyProcedure> mapCreationProcedure(Objective objective, List<ProcedureCreationDTO> list);

    MessageResponse deleteTherapyProcedure(Long id);

    MessageResponse updateTherapyProcedure(Long id, ProcedureCreationDTO dto);

   ObjectiveShowDTO findByObjectiveId(Long objectiveId);

   MessageResponse createProcedureByObjectId(Long objectiveId, List<ProcedureCreationDTO> list);
}
