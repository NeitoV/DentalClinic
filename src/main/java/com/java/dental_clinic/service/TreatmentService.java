package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.*;
import com.java.dental_clinic.data.entity.Service;
import com.java.dental_clinic.data.entity.Treatment;

import java.util.List;

public interface TreatmentService {

    List<TreatmentDTO> filter(String keyword, Long serviceId, String sortOrder);

    List<ServiceDTO> findAllService();

    MessageResponse updateService(ServiceDTO serviceDTO, Long id);

    MessageResponseCustom createService(ServiceDTO serviceDTO);

    MessageResponse deleteService(Long id);

    MessageResponse createTreatment(TreatmentCreationDTO treatmentCreationDTO);

    MessageResponse updateTreatment(Long id, TreatmentCreationDTO treatmentCreationDTO);

    MessageResponse deleteTreatment(Long id);
}
