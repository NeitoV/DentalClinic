package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.patient.PatientDTO;
import com.java.dental_clinic.data.entity.Patient;
import com.java.dental_clinic.data.entity.Staff;
import com.java.dental_clinic.data.enumeration.EPosition;
import com.java.dental_clinic.data.maper.PatientMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.PatientRepository;
import com.java.dental_clinic.repostiory.StaffRepository;
import com.java.dental_clinic.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QueueServiceImpl implements QueueService {
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Map<Long, Queue<Long>> dentistQueues = new HashMap<>();

    public List<PatientDTO> getQueueByDentist(Long dentistId) {
        Queue<Long> queueIdPatient = dentistQueues.getOrDefault(dentistId, new ArrayDeque<>());

        return queueIdPatient
                .stream()
                .map(patientId -> patientMapper.toDTO(patientRepository.findById(patientId).orElse(null)))
                .collect(Collectors.toList());
    }

    public MessageResponse addPatientToQueue(Long staffId, Long patientId) {
        Staff staff = staffRepository.findById(staffId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("staff id:", staffId))
        );

        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("patient id:", patientId))
        );

        if (!staff.getPosition().getId().equals(EPosition.positionDentist)) {
            throw new AccessDeniedException(Collections.singletonMap("message", "staff id is not a dentist"));
        }

        dentistQueues.computeIfAbsent(staffId, k -> new ArrayDeque<>()).add(patientId);
        messagingTemplate.convertAndSend("/topic/queue", getQueueByDentist(staffId));

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    public PatientDTO getNextPatient(Long dentistId) {
        Queue<Long> queueIdPatient = dentistQueues.get(dentistId);

        if (queueIdPatient == null) {
            return null;
        }
        Patient patient = patientRepository.findById(queueIdPatient.poll()).orElse(null);
        messagingTemplate.convertAndSend("/topic/queue", getQueueByDentist(dentistId));

        return patientMapper.toDTO(patient);
    }

    public MessageResponse resetQueueByDentist(Long dentistId) {
        dentistQueues.remove(dentistId);
        messagingTemplate.convertAndSend("/topic/queue", getQueueByDentist(dentistId));

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    public MessageResponse resetAllQueues() {
        dentistQueues.clear();
        messagingTemplate.convertAndSend("/topic/queue", "All queues have been reset");

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }
}
