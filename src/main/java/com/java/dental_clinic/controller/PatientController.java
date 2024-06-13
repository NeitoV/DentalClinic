package com.java.dental_clinic.controller;

import com.java.dental_clinic.data.dto.patient.PatientCreationDTO;
import com.java.dental_clinic.data.dto.patient.PatientDTO;
import com.java.dental_clinic.service.PatientService;
import com.java.dental_clinic.service.ScheduleService;
import com.java.dental_clinic.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<?> createPatient(@RequestBody PatientCreationDTO patientCreationDTO) {

        return new ResponseEntity<>(patientService.createPatient(patientCreationDTO), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@RequestBody PatientDTO patientDTO, @PathVariable Long id) {

        return ResponseEntity.ok(patientService.updatePatient(patientDTO, id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @GetMapping("/filter")
    public ResponseEntity<?> filterPatient(@RequestParam(defaultValue = "0") int pageNumber,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(defaultValue = "") String keyword) {

        return ResponseEntity.ok(patientService.filterPatient(keyword, pageNumber, pageSize));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mail/send")
    public ResponseEntity<?> sendMailActive(@RequestBody String email) throws MessagingException {

        return ResponseEntity.ok(userService.sendMailActiveUser(email));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/mail/active")
    public ResponseEntity<?> activeEmail(@RequestBody Map<String, String> mailConfirm) {
        String email = mailConfirm.get("email");
        String otp = mailConfirm.get("otp");

        return ResponseEntity.ok(userService.activeEmail(email, otp));
    }

}
