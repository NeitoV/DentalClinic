package com.java.dental_clinic.controller;

import com.java.dental_clinic.data.dto.ProcedureCreationDTO;
import com.java.dental_clinic.data.dto.RecordCreationDTO;
import com.java.dental_clinic.data.dto.WorkingDTO;
import com.java.dental_clinic.service.TherapyProcedureService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/procedure")
public class ProcedureController {
    @Autowired
    private TherapyProcedureService therapyProcedureService;


    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/record/{id}")
    public ResponseEntity<?> findByRecordId(@PathVariable Long id) {

        return ResponseEntity.ok(therapyProcedureService.findByMedicalRecordId(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Staff')")
    @PostMapping("/record/{id}")
    public ResponseEntity<?> createProcedureForMedicalRecord(@PathVariable Long id,
                                                             @RequestBody Map<String, List<ProcedureCreationDTO>> list) {

        List<ProcedureCreationDTO> dtos = list.get("procedures");

        return new ResponseEntity<>(therapyProcedureService.createTherapyProcedure(id, dtos), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Staff')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTherapyProcedure(@PathVariable Long id) {

        return ResponseEntity.ok(therapyProcedureService.deleteTherapyProcedure(id));
    }
}
