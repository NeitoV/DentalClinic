package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.patient.PatientDTO;

import java.util.List;

public interface QueueService {
    List<PatientDTO> getQueueByDentist(Long dentistId);

    MessageResponse addPatientToQueue(Long staffId, Long patientId);

    PatientDTO getNextPatient(Long dentistId);

    MessageResponse resetQueueByDentist(Long dentistId);

    MessageResponse resetAllQueues();
}
