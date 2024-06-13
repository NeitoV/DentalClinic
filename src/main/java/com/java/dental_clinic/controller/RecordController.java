package com.java.dental_clinic.controller;

import com.java.dental_clinic.data.dto.RecordCreationDTO;
import com.java.dental_clinic.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/record")
public class RecordController {
    @Autowired
    private MedicalRecordService medicalRecordService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Staff')")
    @PostMapping("")
    public ResponseEntity<?> createMedicalRecord(@RequestBody RecordCreationDTO recordCreationDTO) {

        return new ResponseEntity<>(medicalRecordService.createMedicalRecord(recordCreationDTO), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/patient/{id}")
    public ResponseEntity<?> findByPatientId(@PathVariable Long id) {

        return ResponseEntity.ok(medicalRecordService.findByPatientId(id));
    }
}
