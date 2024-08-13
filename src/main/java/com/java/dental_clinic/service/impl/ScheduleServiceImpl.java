package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.PaginationDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

    private static String subject = "[Nha khoa Võ Tiến] Xác nhận lịch hẹn";
    public static String emailSignature = "\n-----------------------------\n" +
            "Trân trọng,\n" +
            "Nha khoa Võ Tiến\n";

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("'Ngày' d 'tháng' M 'năm' yyyy",
            new Locale("vi", "VN"));


    @Override
    public MessageResponse createScheduleForPatient(Long workingId, String note) {
        Patient patient = patientService.getPatientByToken();
        if (patient.getUser().getEmail() == null) {
            throw new AccessDeniedException(Collections.singletonMap("message", "please active email"));
        }

        Schedule schedule = createSchedule(workingId, note, false);
        schedule.setPatient(patient);
        schedule.setDate(LocalDateTime.now());

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
    public PaginationDTO filter(Boolean isConfirm, Long staffId, int pageNumber, int pageSize, LocalDate date) {
        Page<ScheduleDTO> page = scheduleRepository.filter(staffId, isConfirm, date, PageRequest.of(pageNumber, pageSize))
                .map(schedule -> scheduleMapper.toDTO(schedule));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(), page.getTotalElements(), page.getNumber(), page.getSize());
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
        String periodVN = period.getName().equals("morning") ? "sáng" : "chiều";

        String text = "Lịch hẹn mà bạn đã gửi vào " + schedule.getDate().format(dateFormatter) + " đã được xác nhận." +
                "\n\nCuộc hẹn sẽ diễn ra vào buổi " + periodVN + " vào " +
                schedule.getCalendarWorking().getDate().format(dateFormatter) + " với nha sĩ " +
                schedule.getCalendarWorking().getStaff().getName() + "." +
                "\nCám ơn vì đã sử dụng dịch vụ của chúng tôi!" +
                "\nHẹn gặp bạn vào ngày hôm ấy. " + emailSignature;

        mailService.send(schedule.getPatient().getUser().getEmail(), subject, text);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public MessageResponse confirmScheduleFalse(Long scheduleId) throws MessagingException {

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("schedule id: ", scheduleId))
        );

        checkDentist(schedule.getCalendarWorking().getStaff().getId());
        Period period = periodRepository.findById(schedule.getCalendarWorking().getPeriod().getId()).orElse(null);
        String periodVN = period.getName().equals("morning") ? "sáng" : "chiều";

        String text = "Chúng tôi rất tiếc phải thông báo đến bạn: " +
                "\nCuộc hẹn mà bạn đã gửi vào " +
                schedule.getDate().format(dateFormatter) +
                " đã bị huỷ vì một vài lý do đáng tiếc." +
                "\nThông tin cuộc hẹn bị huỷ: " + " Vào " +
                schedule.getCalendarWorking().getDate().format(dateFormatter) + " trong buổi " + periodVN + " với nha sĩ " +
                schedule.getCalendarWorking().getStaff().getName() + "." +
                "\n\nChúng tôi thành thật gửi lời xin lỗi đến bạn! " +
                "\nMời bạn đặt lại lịch hẹn khác hoặc liên hệ với chúng tôi để được hỗ trợ nhanh nhất." +
                "\nHẹn gặp bạn vào thời gian sớm nhất." + emailSignature;

        mailService.send(schedule.getPatient().getUser().getEmail(), subject, text);

        scheduleRepository.delete(schedule);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public List<ScheduleDTO> findScheduleByPatient(Boolean isConfirm) {
        Patient patient = patientService.getPatientByToken();

        return scheduleRepository.findForPatient(patient.getId(), isConfirm)
                .stream()
                .map(schedule -> scheduleMapper.toDTO(schedule))
                .collect(Collectors.toList());
    }

    private void checkDentist(Long staffId) {
        User user = userService.getUserByToken();
        if (user.getRole().getId() == ERole.roleStaff) {
            Staff staff = staffRepository.findByUserId(user.getId()).orElseThrow(
                    () -> new ResourceNotFoundException(Collections.singletonMap("message", "staff not exists"))
            );

            if (staff.getPosition().getId() == EPosition.positionDentist) {
                if (staffId != staff.getId()) {
                    throw new AccessDeniedException(Collections.singletonMap("message", "this is not yours"));
                }
            }
        }
    }
}
