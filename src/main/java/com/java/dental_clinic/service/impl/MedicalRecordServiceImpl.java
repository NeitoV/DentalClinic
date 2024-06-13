package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.*;
import com.java.dental_clinic.data.entity.*;
import com.java.dental_clinic.data.enumeration.EPosition;
import com.java.dental_clinic.data.enumeration.ERole;
import com.java.dental_clinic.data.maper.ProcedureMapper;
import com.java.dental_clinic.data.maper.RecordMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.PatientRepository;
import com.java.dental_clinic.repostiory.ProcedureRepository;
import com.java.dental_clinic.repostiory.RecordRepository;
import com.java.dental_clinic.repostiory.TreatmentRepository;
import com.java.dental_clinic.service.MedicalRecordService;
import com.java.dental_clinic.service.StaffService;
import com.java.dental_clinic.service.TherapyProcedureService;
import com.java.dental_clinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public MessageResponse createMedicalRecord(RecordCreationDTO recordCreationDTO) {

        Staff staff = staffService.getStaffByToken();
        if (staff.getPosition().getId() != EPosition.positionDentist) {
            throw new AccessDeniedException(Collections.singletonMap("message: ", "you aren't a dentist"));
        }

        Patient patient = patientRepository.findById(recordCreationDTO.getPatientId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        Collections.singletonMap("patient id: ", recordCreationDTO.getPatientId())));

        MedicalRecord medicalRecord = recordMapper.toEntity(recordCreationDTO);

        medicalRecord.setExaminationDate(LocalDate.now());
        medicalRecord.setStaff(staff);
        medicalRecord.setPatient(patient);

        MedicalRecord saved = recordRepository.save(medicalRecord);

        List<TherapyProcedure> list = therapyProcedureService.mapCreationProcedure(
                saved, recordCreationDTO.getProcedureCreationDTOS());

        procedureRepository.saveAll(list);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public List<RecordShowDTO> findByPatientId(Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("patient id: ", patientId))
        );

        User user = userService.getUserByToken();

        if(user.getRole().getId() == ERole.rolePatient && patient.getUser().getId() != user.getId()) {
            throw new AccessDeniedException(
                    Collections.singletonMap("message: ", "You are not allowed to view someone else's medical records"));
        }

        List<RecordShowDTO> list = recordRepository.findAllByPatientId(patientId).stream().map(
                medicalRecord -> recordMapper.toDTOShow(medicalRecord)
        ).collect(Collectors.toList());

        return list;
    }
}
