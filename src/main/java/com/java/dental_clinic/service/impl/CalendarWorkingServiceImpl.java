package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.WorkingDTO;
import com.java.dental_clinic.data.dto.WorkingDTOWithID;
import com.java.dental_clinic.data.dto.WorkingShowDTO;
import com.java.dental_clinic.data.entity.CalendarWorking;
import com.java.dental_clinic.data.entity.Period;
import com.java.dental_clinic.data.entity.Staff;
import com.java.dental_clinic.data.maper.WorkingMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.PeriodRepository;
import com.java.dental_clinic.repostiory.WorkingRepository;
import com.java.dental_clinic.service.CalendarWorkingService;
import com.java.dental_clinic.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarWorkingServiceImpl implements CalendarWorkingService {
    @Autowired
    private StaffService staffService;
    @Autowired
    private WorkingRepository workingRepository;
    @Autowired
    private PeriodRepository periodRepository;
    @Autowired
    private WorkingMapper workingMapper;


    @Override
    public MessageResponse createWorkingCalendar(List<WorkingDTO> workingDTOList) {
        Staff staff = staffService.getStaffByToken();

        for (WorkingDTO workingDTO : workingDTOList) {

            if (workingRepository.existsByPeriodIdAndDateAndStaffId( //check existed
                    workingDTO.getPeriodId(), workingDTO.getDate(), staff.getId()))
                throw new AccessDeniedException(
                        Collections.singletonMap("message: ", "there have a existed date and period"));
            else { //create new

                CalendarWorking calendarWorking = new CalendarWorking();

                Period period = periodRepository.findById(workingDTO.getPeriodId()).orElseThrow(
                        () -> new ResourceNotFoundException(
                                Collections.singletonMap("period id: ", workingDTO.getPeriodId()))
                );

                calendarWorking.setPeriod(period);
                calendarWorking.setDate(workingDTO.getDate());
                calendarWorking.setStaff(staff);

                workingRepository.save(calendarWorking);
            }
        }

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public List<WorkingShowDTO> filter(String keyword, LocalDate date, Long periodId) {

        List<WorkingShowDTO> list = workingRepository.filter(date, keyword, periodId).stream()
                .map(calendarWorking -> workingMapper.toDTOShow(calendarWorking))
                .collect(Collectors.toList());


        return list;
    }

    @Override
    public MessageResponse deleteWorkingCalendar(Long periodId, LocalDate date) {
        Staff staff = staffService.getStaffByToken();

        CalendarWorking calendarWorking = workingRepository.findByPeriodIdAndStaffIdAndDate(
               periodId, staff.getId(), date).orElseThrow(
                        () -> new ResourceNotFoundException(
                                Collections.singletonMap("message: ", "date and period don't exist")));

        if (staff.getId() != calendarWorking.getStaff().getId()) {
            throw new AccessDeniedException(
                    Collections.singletonMap("message: ", "you can't delete someone else's working calendar"));
        }

        workingRepository.delete(calendarWorking);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public List<WorkingDTOWithID> findByWeek(int year, int week) {
        Staff staff = staffService.getStaffByToken();

        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);

        LocalDate startDate = firstDayOfYear
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 1);

        LocalDate endDate = startDate.plusDays(6);

        List<WorkingDTOWithID> list = workingRepository.findByDateRangeAndStaffId(startDate, endDate, staff.getId())
                .stream()
                .map(calendarWorking -> workingMapper.toDTO(calendarWorking))
                .collect(Collectors.toList());

        return list;
    }


}
