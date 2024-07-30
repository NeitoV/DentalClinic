package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ObjectiveDTO;
import com.java.dental_clinic.data.dto.ObjectivesCreationDTO;
import com.java.dental_clinic.data.entity.MedicalRecord;
import com.java.dental_clinic.data.entity.Objective;
import com.java.dental_clinic.data.entity.Staff;
import com.java.dental_clinic.data.enumeration.EStatus;
import com.java.dental_clinic.data.maper.ObjectiveMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.ObjectiveRepository;
import com.java.dental_clinic.repostiory.ProcedureRepository;
import com.java.dental_clinic.repostiory.RecordRepository;
import com.java.dental_clinic.service.MedicalRecordService;
import com.java.dental_clinic.service.ObjectiveService;
import com.java.dental_clinic.service.StaffService;
import com.java.dental_clinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObjectiveServiceImpl implements ObjectiveService {
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private MedicalRecordService medicalRecordService;
    @Autowired
    private ObjectiveRepository objectiveRepository;
    @Autowired
    private ProcedureRepository procedureRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectiveMapper objectiveMapper;

    @Override
    public MessageResponse createObjectiveForRecord(Long recordId, List<ObjectivesCreationDTO> list) {
        MedicalRecord medicalRecord = recordRepository.findById(recordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("record id: ", recordId))
        );

        userService.checkStaff(medicalRecord.getStaff().getId());

        if (medicalRecord.getStatus().equals(EStatus.DONE.toString())) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't update record done"));
        }

        medicalRecordService.createListObjectives(list, medicalRecord);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public MessageResponse deleteObjective(Long id) {
        Objective objective = objectiveRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id: ", id))
        );

        if (objective.getMedicalRecord().getStatus().equals(EStatus.DONE.toString())) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't update record done"));
        }

        userService.checkStaff(objective.getMedicalRecord().getStaff().getId());

        procedureRepository.deleteAllByObjectiveId(id);
        objectiveRepository.deleteById(id);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public List<ObjectiveDTO> findAllByRecordId(Long recordId) {

        return objectiveRepository.findAllByMedicalRecordId(recordId)
                .stream()
                .map(objective -> objectiveMapper.toDTO(objective))
                .collect(Collectors.toList());
    }


}
