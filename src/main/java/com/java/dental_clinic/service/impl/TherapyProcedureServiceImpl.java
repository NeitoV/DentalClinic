package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ObjectiveShowDTO;
import com.java.dental_clinic.data.dto.procedure.ProcedureCreationDTO;
import com.java.dental_clinic.data.dto.procedure.ProcedureRecordShowDTO;
import com.java.dental_clinic.data.dto.procedure.ProcedureShowDTO;
import com.java.dental_clinic.data.entity.*;
import com.java.dental_clinic.data.enumeration.EStatus;
import com.java.dental_clinic.data.maper.ObjectiveMapper;
import com.java.dental_clinic.data.maper.ProcedureMapper;
import com.java.dental_clinic.data.maper.RecordMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.*;
import com.java.dental_clinic.service.StaffService;
import com.java.dental_clinic.service.TherapyProcedureService;
import com.java.dental_clinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TherapyProcedureServiceImpl implements TherapyProcedureService {
    @Autowired
    private ProcedureRepository procedureRepository;
    @Autowired
    private ProcedureMapper procedureMapper;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TreatmentRepository treatmentRepository;
    @Autowired
    private StaffService staffService;
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private ObjectiveRepository objectiveRepository;
    @Autowired
    private ObjectiveMapper objectiveMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public ProcedureRecordShowDTO findByMedicalRecordId(Long recordId) {

        MedicalRecord record = recordRepository.findById(recordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("record id: ", recordId))
        );

        List<ObjectiveShowDTO> objectiveShowDTOS = new ArrayList<>();
        List<Objective> objectives = objectiveRepository.findAllByMedicalRecordId(recordId);
        for (Objective objective : objectives) {

            List<ProcedureShowDTO> list = procedureRepository.findAllByObjectiveId(objective.getId()).stream().map(
                    therapyProcedure -> procedureMapper.toDTOShow(therapyProcedure)
            ).collect(Collectors.toList());

            ObjectiveShowDTO objectiveShowDTO = objectiveMapper.toDTOShow(objective);
            objectiveShowDTO.setProcedureShowDTOS(list);

            objectiveShowDTOS.add(objectiveShowDTO);
        }

        ProcedureRecordShowDTO recordShowDTO = new ProcedureRecordShowDTO();

        recordShowDTO.setObjectiveShowDTOS(objectiveShowDTOS);
        recordShowDTO.setRecordShowDTO(recordMapper.toDTOShow(record));

        return recordShowDTO;
    }


    private void catchExceptionMedical(Objective objective) {
        MedicalRecord medicalRecord = objective.getMedicalRecord();

        if (medicalRecord.getStatus().equals(EStatus.DONE.toString())) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't update record done"));
        }

        userService.checkStaff(objective.getMedicalRecord().getStaff().getId());
    }

    public List<TherapyProcedure> mapCreationProcedure(Objective objective, List<ProcedureCreationDTO> list) {

        return list.stream()
                .map(dto -> createTherapyProcedure(dto, objective))
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse deleteTherapyProcedure(Long id) {
        TherapyProcedure procedure = procedureRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("procedure id: ", id))
        );

        if (invoiceRepository.existsByObjectiveId(procedure.getObjective().getId())) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't update objective has invoice"));
        }

        catchExceptionMedical(procedure.getObjective());
        procedureRepository.delete(procedure);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    private TherapyProcedure createTherapyProcedure(ProcedureCreationDTO dto, Objective objective) {

        Treatment treatment = treatmentRepository.findById(dto.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        Collections.singletonMap("treatment id", dto.getTreatmentId())));

        TherapyProcedure therapyProcedure = procedureMapper.toEntity(dto);
        therapyProcedure.setObjective(objective);
        therapyProcedure.setTreatment(treatment);

        return therapyProcedure;
    }

    public MessageResponse updateTherapyProcedure(Long id, ProcedureCreationDTO dto) {
        TherapyProcedure procedure = procedureRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("procedure id: ", id))
        );

        if (invoiceRepository.existsByObjectiveId(procedure.getObjective().getId())) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't update objective has invoice"));
        }

        catchExceptionMedical(procedure.getObjective());
        TherapyProcedure update = createTherapyProcedure(dto, procedure.getObjective());

        update.setId(procedure.getId());
        procedureRepository.save(update);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public ObjectiveShowDTO findByObjectiveId(Long objectiveId) {
        Objective objective = objectiveRepository.findById(objectiveId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id: ", objectiveId))
        );

        List<ProcedureShowDTO> procedureShowDTOS = procedureRepository.findAllByObjectiveId(objectiveId)
                .stream()
                .map(therapyProcedure -> procedureMapper.toDTOShow(therapyProcedure))
                .collect(Collectors.toList());

        ObjectiveShowDTO objectiveShowDTO = objectiveMapper.toDTOShow(objective);
        objectiveShowDTO.setProcedureShowDTOS(procedureShowDTOS);

        return objectiveShowDTO;

    }

    @Override
    public MessageResponse createProcedureByObjectId(Long objectiveId, List<ProcedureCreationDTO> list) {
        Objective objective = objectiveRepository.findById(objectiveId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("objective id: ", objectiveId))
        );

        userService.checkStaff(objective.getMedicalRecord().getStaff().getId());

        if (objective.getMedicalRecord().getStatus().equals(EStatus.DONE.toString())) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't update record done"));
        }

        if (invoiceRepository.existsByObjectiveId(objectiveId)) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't update objective has invoice"));
        }

        List<TherapyProcedure> procedureList = mapCreationProcedure(objective, list);
        procedureRepository.saveAll(procedureList);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }
}
