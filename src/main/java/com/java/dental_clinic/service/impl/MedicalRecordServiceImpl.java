package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.*;
import com.java.dental_clinic.data.entity.*;
import com.java.dental_clinic.data.enumeration.EPosition;
import com.java.dental_clinic.data.enumeration.ERole;
import com.java.dental_clinic.data.enumeration.EStatus;
import com.java.dental_clinic.data.maper.ObjectiveMapper;
import com.java.dental_clinic.data.maper.ProcedureMapper;
import com.java.dental_clinic.data.maper.RecordMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.*;
import com.java.dental_clinic.service.MedicalRecordService;
import com.java.dental_clinic.service.StaffService;
import com.java.dental_clinic.service.TherapyProcedureService;
import com.java.dental_clinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {
    @Autowired
    private StaffService staffService;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ProcedureRepository procedureRepository;
    @Autowired
    private ProcedureMapper procedureMapper;
    @Autowired
    private TreatmentRepository treatmentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TherapyProcedureService therapyProcedureService;
    @Autowired
    private ObjectiveMapper objectiveMapper;
    @Autowired
    private ObjectiveRepository objectiveRepository;

    @Override
    public MessageResponse createMedicalRecord(RecordCreationDTO recordCreationDTO) {

        Staff staff = staffService.getStaffByToken();
        if (staff.getPosition().getId() != EPosition.positionDentist) {
            throw new AccessDeniedException(Collections.singletonMap("message", "you aren't a dentist"));
        }

        Patient patient = patientRepository.findById(recordCreationDTO.getPatientId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        Collections.singletonMap("patient id: ", recordCreationDTO.getPatientId())));

        MedicalRecord medicalRecord = recordMapper.toEntity(recordCreationDTO);

        medicalRecord.setExaminationDate(LocalDate.now());
        medicalRecord.setStaff(staff);
        medicalRecord.setPatient(patient);
        medicalRecord.setStatus(EStatus.ONGOING.toString());

        MedicalRecord saved = recordRepository.save(medicalRecord);

       createListObjectives(recordCreationDTO.getObjectivesCreationDTOS(), saved);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public PaginationDTO findByPatientId(Long patientId, int pageNumber, int pageSize) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("patient id: ", patientId))
        );

        User user = userService.getUserByToken();

        if (user.getRole().getId() == ERole.rolePatient && patient.getUser().getId() != user.getId()) {
            throw new AccessDeniedException(
                    Collections.singletonMap("message", "You are not allowed to view someone else's medical records"));
        }

        Page<RecordShowDTO> page = recordRepository.findAllByPatientId(patientId, PageRequest.of(pageNumber, pageSize))
                .map(medicalRecord -> recordMapper.toDTOShow(medicalRecord));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public MessageResponse deleteRecord(Long recordId) {
        MedicalRecord record = recordRepository.findById(recordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id: ", recordId))
        );

        userService.checkStaff(record.getStaff().getId());

        procedureRepository.deleteByMedicalRecordId(recordId);
        objectiveRepository.deleteAllByMedicalRecordId(recordId);
        recordRepository.deleteById(recordId);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public MessageResponse updateStatusDone(Long recordId) {
        MedicalRecord record = recordRepository.findById(recordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id: ", recordId))
        );

        userService.checkStaff(record.getStaff().getId());

        record.setStatus(EStatus.DONE.toString());
        recordRepository.save(record);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public MessageResponse reOpenMedicalRecord(Long recordId) {
        MedicalRecord record = recordRepository.findById(recordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id: ", recordId))
        );

        userService.checkStaff(record.getStaff().getId());

        record.setStatus(EStatus.ONGOING.toString());
        recordRepository.save(record);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    public void createListObjectives(List<ObjectivesCreationDTO> objectivesCreationDTOS, MedicalRecord record) {
        for (ObjectivesCreationDTO creationDTO : objectivesCreationDTOS) {

            Objective objective = objectiveMapper.toEntity(creationDTO);
            objective.setMedicalRecord(record);
            objective.setExaminationDate(LocalDate.now());

            Objective objectiveSaved = objectiveRepository.save(objective);

            List<TherapyProcedure> list = therapyProcedureService.mapCreationProcedure(
                    objectiveSaved, creationDTO.getProcedureCreationDTOList());

            procedureRepository.saveAll(list);
        }
    }

    @Override
    public PaginationDTO findByToken(int pageNumber, int pageSize) {
        User user = userService.getUserByToken();
        Patient patient = patientRepository.findByUserId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("message", "user is not existed"))
        );

        return findByPatientId(patient.getId(), pageNumber, pageSize);
    }
}
