package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface CalendarWorkingService {
    @Transactional
    MessageResponse createWorkingCalendar(List<WorkingDTO> workingDTOList);

    List<WorkingShowDTO> filter(String keyword, LocalDate date, Long periodId);

    MessageResponse deleteWorkingCalendar(Long periodId, LocalDate date);

    List<WorkingDTOWithID> findByWeek(int year, int week);
}
