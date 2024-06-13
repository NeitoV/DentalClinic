package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.ServiceDTO;
import com.java.dental_clinic.data.dto.TreatmentDTO;
import com.java.dental_clinic.data.maper.ServiceMapper;
import com.java.dental_clinic.data.maper.TreatmentMapper;
import com.java.dental_clinic.repostiory.ServiceRepository;
import com.java.dental_clinic.repostiory.TreatmentRepository;
import com.java.dental_clinic.service.TreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<TreatmentDTO> filter(String keyword, Long serviceId, String sortOrder) {

        return treatmentRepository.filter(keyword, serviceId, sortOrder).stream()
                .map(treatment -> treatmentMapper.toDTO(treatment)).collect(Collectors.toList());
    }

    public List<ServiceDTO> findAllService() {

        return serviceRepository.findAll().stream()
                .map(service -> serviceMapper.toDTO(service)).collect(Collectors.toList());
    }
}
