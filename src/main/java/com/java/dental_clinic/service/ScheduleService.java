package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ScheduleDTO;

import javax.mail.MessagingException;
import java.util.List;

public interface ScheduleService {
    MessageResponse createScheduleForPatient(Long workingId, String note);

    MessageResponse createScheduleForStaff(Long workingId, String note, Long patientId);

    List<ScheduleDTO> filter(Boolean isConfirm, Long staffId);

    MessageResponse confirmScheduleTrue(Long scheduleId) throws MessagingException;

    MessageResponse confirmScheduleFalse(Long scheduleId) throws MessagingException;

}
