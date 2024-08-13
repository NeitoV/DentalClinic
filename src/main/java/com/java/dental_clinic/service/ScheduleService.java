package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.PaginationDTO;
import com.java.dental_clinic.data.dto.ScheduleDTO;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    MessageResponse createScheduleForPatient(Long workingId, String note);

    MessageResponse createScheduleForStaff(Long workingId, String note, Long patientId);

    PaginationDTO filter(Boolean isConfirm, Long staffId, int pageNumber, int pageSize, LocalDate date);

    MessageResponse confirmScheduleTrue(Long scheduleId) throws MessagingException;

    MessageResponse confirmScheduleFalse(Long scheduleId) throws MessagingException;

    List<ScheduleDTO> findScheduleByPatient(Boolean isConfirm);
}
