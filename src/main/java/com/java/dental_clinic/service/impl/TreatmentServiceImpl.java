package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.*;
import com.java.dental_clinic.data.entity.Treatment;
import com.java.dental_clinic.data.maper.ServiceMapper;
import com.java.dental_clinic.data.maper.TreatmentMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.ProcedureRepository;
import com.java.dental_clinic.repostiory.ServiceRepository;
import com.java.dental_clinic.repostiory.TreatmentRepository;
import com.java.dental_clinic.service.TreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TreatmentServiceImpl implements TreatmentService {
    @Autowired
    private TreatmentRepository treatmentRepository;
    @Autowired
    private TreatmentMapper treatmentMapper;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private ProcedureRepository procedureRepository;

    @Override
    public List<TreatmentDTO> filter(String keyword, Long serviceId, String sortOrder) {

        return treatmentRepository.filter(keyword, serviceId, sortOrder).stream()
                .map(treatment -> treatmentMapper.toDTO(treatment)).collect(Collectors.toList());
    }

    public List<ServiceDTO> findAllService() {

        return serviceRepository.findAll().stream()
                .map(service -> serviceMapper.toDTO(service)).collect(Collectors.toList());
    }

    @Override
    public MessageResponse updateService(ServiceDTO serviceDTO, Long id) {
        com.java.dental_clinic.data.entity.Service service = serviceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id", id))
        );
        service.setName(serviceDTO.getName());

        serviceRepository.save(service);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public MessageResponseCustom createService(ServiceDTO serviceDTO) {

        com.java.dental_clinic.data.entity.Service service =  serviceRepository.save(serviceMapper.toEntity(serviceDTO));

        return new MessageResponseCustom(HttpServletResponse.SC_CREATED, "successfully", service.getId());
    }

    @Override
    public MessageResponse deleteService(Long id) {
        if(treatmentRepository.existsByServiceId(id)) {
            throw new AccessDeniedException(Collections.singletonMap("message", "service has been used"));
        }
        serviceRepository.deleteById(id);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public MessageResponse createTreatment(TreatmentCreationDTO treatmentCreationDTO) {

        Treatment treatment = treatmentMapper.toEntity(treatmentCreationDTO);

        com.java.dental_clinic.data.entity.Service service = serviceRepository
                .findById(treatmentCreationDTO.getServiceId())
                .orElseThrow(() ->  new ResourceNotFoundException(
                        Collections.singletonMap("service id", treatmentCreationDTO.getServiceId())));

        treatment.setService(service);
        treatmentRepository.save(treatment);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public MessageResponse updateTreatment(Long id, TreatmentCreationDTO treatmentCreationDTO) {
        Treatment treatment = treatmentRepository.findById(id).orElseThrow(
                () ->  new ResourceNotFoundException(Collections.singletonMap("treatment id", id))
        );

        Treatment updated = treatmentMapper.toEntity(treatmentCreationDTO);

        com.java.dental_clinic.data.entity.Service service = serviceRepository
                .findById(treatmentCreationDTO.getServiceId())
                .orElseThrow(() ->  new ResourceNotFoundException(
                        Collections.singletonMap("service id", treatmentCreationDTO.getServiceId())));

        updated.setId(treatment.getId());
        updated.setService(service);
        treatmentRepository.save(updated);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public MessageResponse deleteTreatment(Long id) {
        if(procedureRepository.existsByTreatmentId(id)) {
            throw new AccessDeniedException(Collections.singletonMap("message","treatment has been used"));
        }
        treatmentRepository.deleteById(id);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

}
