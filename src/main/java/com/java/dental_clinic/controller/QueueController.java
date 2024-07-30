package com.java.dental_clinic.controller;

import com.java.dental_clinic.service.QueueService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/queue")
public class QueueController {
    @Autowired
    private QueueService queueService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @GetMapping("/staff/{staffId}")
    ResponseEntity<?> getQueueByDentist(@PathVariable Long staffId) {

        return ResponseEntity.ok(queueService.getQueueByDentist(staffId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @PostMapping("/staff/{staffId}/patient/{patientId}")
    ResponseEntity<?> addPatientToQueue(@PathVariable Long staffId, @PathVariable Long patientId) {

        return new ResponseEntity<>(queueService.addPatientToQueue(staffId, patientId), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @GetMapping("/next/staff/{staffId}")
    ResponseEntity<?> getNextPatient(@PathVariable Long staffId) {

        return ResponseEntity.ok(queueService.getNextPatient(staffId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @DeleteMapping("/staff/{staffId}")
    ResponseEntity<?> resetQueueByStaff(@PathVariable Long staffId) {

        return ResponseEntity.ok(queueService.resetQueueByDentist(staffId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @DeleteMapping("")
    ResponseEntity<?> resetAllQueues() {

        return  ResponseEntity.ok(queueService.resetAllQueues());
    }
}
