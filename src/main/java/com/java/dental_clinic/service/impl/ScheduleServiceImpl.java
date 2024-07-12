package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ScheduleDTO;
import com.java.dental_clinic.data.entity.*;
import com.java.dental_clinic.data.enumeration.EPosition;
import com.java.dental_clinic.data.enumeration.ERole;
import com.java.dental_clinic.data.maper.ScheduleMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.*;
import com.java.dental_clinic.service.MailService;
import com.java.dental_clinic.service.PatientService;
import com.java.dental_clinic.service.ScheduleService;
import com.java.dental_clinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private WorkingRepository workingRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ScheduleMapper scheduleMapper;
    @Autowired
    private PeriodRepository periodRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    @Autowired
    private StaffRepository staffRepository;

    private static String subject = "RESULT SCHEDULE";

    @Override
    public MessageResponse createScheduleForPatient(Long workingId, String note) {
        Patient patient = patientService.getPatientByToken();
        if (patient.getUser().getEmail() == null) {
            throw new AccessDeniedException(Collections.singletonMap("message: ", "please active email"));
        }

        Schedule schedule = createSchedule(workingId, note, false);
        schedule.setPatient(patient);

        scheduleRepository.save(schedule);

        return new MessageResponse(HttpServletResponse.SC_CREATED,
                "please check your email for getting result schedule");
    }

    private Schedule createSchedule(Long workingId, String note, boolean isConfirm) {
        CalendarWorking calendarWorking = workingRepository.findById(workingId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("working calendar: ", workingId))
        );

        Schedule schedule = new Schedule();

        schedule.setDate(LocalDateTime.now());
        schedule.setNote(note);
        schedule.setCalendarWorking(calendarWorking);
        schedule.setConfirm(isConfirm);

        return schedule;
    }

    @Override
    public MessageResponse createScheduleForStaff(Long workingId, String note, Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("patient Id: ", patientId))
        );

        Schedule schedule = createSchedule(workingId, note, true);
        schedule.setPatient(patient);

        scheduleRepository.save(schedule);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public List<ScheduleDTO> filter(Boolean isConfirm, Long staffId) {
        List<ScheduleDTO> scheduleDTOS = scheduleRepository.filter(staffId, isConfirm)
                .stream()
                .map(schedule -> scheduleMapper.toDTO(schedule))
                .collect(Collectors.toList());

        return scheduleDTOS;
    }

    @Override
    public MessageResponse confirmScheduleTrue(Long scheduleId) throws MessagingException {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("schedule id: ", scheduleId))
        );

        checkDentist(schedule.getCalendarWorking().getStaff().getId());

        schedule.setConfirm(true);
        scheduleRepository.save(schedule);

        Period period = periodRepository.findById(schedule.getCalendarWorking().getPeriod().getId()).orElse(null);

        String text = "Your schedule that you sent at " + schedule.getDate() + " have been confirmed." +
                "\nThe schedule will begin in the " + period.getName() + " at " + schedule.getCalendarWorking().getDate() +
                "\nThank you for using our service! " +
                "\nSee you soon.";

        mailService.send(schedule.getPatient().getUser().getEmail(), subject, text);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public MessageResponse confirmScheduleFalse(Long scheduleId) throws MessagingException {

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("schedule id: ", scheduleId))
        );

        checkDentist(schedule.getCalendarWorking().getStaff().getId());

        String text = "We regret to inform to you: The schedule that you sent at " + schedule.getDate() +
                " have been refused. You should send it again." +
                "\nThank you for using our service!" +
                "\nSee you soon.";

        mailService.send(schedule.getPatient().getUser().getEmail(), subject, text);

        scheduleRepository.delete(schedule);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public List<ScheduleDTO> findScheduleByPatient() {
        Patient patient = patientService.getPatientByToken();

        return scheduleRepository.findAllByPatientId(patient.getId())
                .stream()
                .map(schedule -> scheduleMapper.toDTO(schedule))
                .collect(Collectors.toList());
    }

    private void checkDentist(Long staffId) {
        User user = userService.getUserByToken();
        if(user.getRole().getId() == ERole.roleStaff ) {
            Staff staff = staffRepository.findByUserId(user.getId()).orElseThrow(
                    () -> new ResourceNotFoundException(Collections.singletonMap("message: ", "staff not exists"))
            );

            if(staff.getPosition().getId() == EPosition.positionDentist) {
                if(staffId != staff.getId()) {
                    throw  new AccessDeniedException(Collections.singletonMap("message: ", "this is not yours"));
                }
            }
        }
    }
}
