package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ProcedureCreationDTO;
import com.java.dental_clinic.data.dto.ProcedureRecordShowDTO;
import com.java.dental_clinic.data.dto.ProcedureShowDTO;
import com.java.dental_clinic.data.entity.MedicalRecord;
import com.java.dental_clinic.data.entity.Staff;
import com.java.dental_clinic.data.entity.TherapyProcedure;
import com.java.dental_clinic.data.entity.Treatment;
import com.java.dental_clinic.data.maper.ProcedureMapper;
import com.java.dental_clinic.data.maper.RecordMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.PeriodRepository;
import com.java.dental_clinic.repostiory.ProcedureRepository;
import com.java.dental_clinic.repostiory.RecordRepository;
import com.java.dental_clinic.repostiory.TreatmentRepository;
import com.java.dental_clinic.service.StaffService;
import com.java.dental_clinic.service.TherapyProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
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

    @Override
    public ProcedureRecordShowDTO findByMedicalRecordId(Long recordId) {

        List<ProcedureShowDTO> list = procedureRepository.findAllByMedicalRecordId(recordId).stream().map(
                therapyProcedure -> procedureMapper.toDTOShow(therapyProcedure)
        ).collect(Collectors.toList());

        MedicalRecord record = recordRepository.findById(recordId).orElse(null);

        ProcedureRecordShowDTO recordShowDTO = new ProcedureRecordShowDTO();
        recordShowDTO.setProcedureShowDTOS(list);
        recordShowDTO.setRecordShowDTO(recordMapper.toDTOShow(record));

        return recordShowDTO;
    }

    @Override
    public MessageResponse createTherapyProcedure(Long recordId, List<ProcedureCreationDTO> list) {

        MedicalRecord medicalRecord = recordRepository.findById(recordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("record id: ", recordId))
        );

        Staff staff = staffService.getStaffByToken();
        if(staff.getId() != medicalRecord.getStaff().getId()) {

            throw new AccessDeniedException(Collections.singletonMap("message: ", "This patient doesn't belong to you"));
        }

        List<TherapyProcedure> procedureList =  mapCreationProcedure(medicalRecord, list);
        procedureRepository.saveAll(procedureList);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    public  List<TherapyProcedure> mapCreationProcedure(MedicalRecord medicalRecord, List<ProcedureCreationDTO> list) {

        List<TherapyProcedure> procedureList = list.stream()
                .map(dto -> createTherapyProcedure(dto, medicalRecord))
                .collect(Collectors.toList());

        return procedureList;
    }

    @Override
    public MessageResponse deleteTherapyProcedure(Long id) {
        TherapyProcedure procedure = procedureRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("procedure id: ", id))
        );

        Staff staff = staffService.getStaffByToken();
        if(staff.getId() != procedure.getMedicalRecord().getStaff().getId()) {

            throw new AccessDeniedException(Collections.singletonMap("message: ", "This patient doesn't belong to you"));
        }

        procedureRepository.delete(procedure);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    private TherapyProcedure createTherapyProcedure(ProcedureCreationDTO dto, MedicalRecord medicalRecord) {

        Treatment treatment = treatmentRepository.findById(dto.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        Collections.singletonMap("treatment id", dto.getTreatmentId())));

        TherapyProcedure therapyProcedure = procedureMapper.toEntity(dto);
        therapyProcedure.setMedicalRecord(medicalRecord);
        therapyProcedure.setExaminationDate(LocalDate.now());
        therapyProcedure.setTreatment(treatment);

        return therapyProcedure;
    }

}
