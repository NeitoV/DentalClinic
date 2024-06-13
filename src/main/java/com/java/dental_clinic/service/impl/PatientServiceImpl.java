package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.PaginationDTO;
import com.java.dental_clinic.data.dto.patient.PatientCreationDTO;
import com.java.dental_clinic.data.dto.patient.PatientDTO;
import com.java.dental_clinic.data.entity.Patient;
import com.java.dental_clinic.data.entity.User;
import com.java.dental_clinic.data.enumeration.ERole;
import com.java.dental_clinic.data.maper.PatientMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.PatientRepository;
import com.java.dental_clinic.service.PatientService;
import com.java.dental_clinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    private UserService userService;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private PatientRepository patientRepository;

    @Override
    public MessageResponse createPatient(PatientCreationDTO patientCreationDTO) {
        User user = userService.createUser(patientCreationDTO.getLoginDTO(), ERole.rolePatient);

        Patient patient = patientMapper.toEntity(patientCreationDTO.getPatientDTO());
        patient.setUser(user);
        patientRepository.save(patient);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public MessageResponse updatePatient(PatientDTO patientDTO, Long id) {

        User user = userService.getUserByToken();

        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id:", id))
        );

        if (user.getRole().getId() == ERole.rolePatient && user.getId() != patient.getUser().getId()) {

            throw new AccessDeniedException(
                    Collections.singletonMap("message: ", "You can't  update another user's account"));
        }

        Patient update = patientMapper.toEntity(patientDTO);
        update.setUser(patient.getUser());
        update.setId(id);

        patientRepository.save(update);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public PaginationDTO filterPatient(String keyword, int pageNumber, int pageSize) {
        Page<PatientDTO> page = patientRepository.filter(keyword, PageRequest.of(pageNumber, pageSize))
                .map(patient -> patientMapper.toDTO(patient));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public Patient getPatientByToken() {
        User user = userService.getUserByToken();

        Patient patient = patientRepository.findByUserId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("message: ", "user isn't existed"))
        );

        return patient;
    }


}
