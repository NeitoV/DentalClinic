package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.ServiceDTO;
import com.java.dental_clinic.data.dto.TreatmentDTO;

import java.util.List;

public interface TreatmentService {

    List<TreatmentDTO> filter(String keyword, Long serviceId, String sortOrder);

    List<ServiceDTO> findAllService();
}
