package com.java.dental_clinic.controller;

import com.java.dental_clinic.service.ScheduleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Patient')")
    @PostMapping("/patient")
    public ResponseEntity<?> createScheduleForPatient(@RequestBody Map<String, String> creation) {
        String note = creation.get("note");
        Long workingId = Long.valueOf(creation.get("workingId"));

        return ResponseEntity.ok(scheduleService.createScheduleForPatient(workingId, note));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @PostMapping("/staff")
    public ResponseEntity<?> createScheduleForStaff(@RequestBody Map<String, String> creation) {
        String note = creation.get("note");
        Long workingId = Long.valueOf(creation.get("workingId"));
        Long patientId = Long.valueOf(creation.get("patientId"));

        return  ResponseEntity.ok(scheduleService.createScheduleForStaff(workingId, note, patientId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @GetMapping("")
    public ResponseEntity<?> filterSchedule(@RequestParam(required = false) Boolean isConfirm,
                                            @RequestParam(defaultValue = "0") Long staffId) {

        return ResponseEntity.ok(scheduleService.filter(isConfirm, staffId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @PutMapping("/{id}/true")
    public ResponseEntity<?> confirmScheduleTrue(@PathVariable Long id) throws MessagingException {

        return ResponseEntity.ok(scheduleService.confirmScheduleTrue(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @PutMapping("/{id}/false")
    public ResponseEntity<?> confirmScheduleFalse(@PathVariable Long id) throws MessagingException {

        return ResponseEntity.ok(scheduleService.confirmScheduleFalse(id));
    }
}
