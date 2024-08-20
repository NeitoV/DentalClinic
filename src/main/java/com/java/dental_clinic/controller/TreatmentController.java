package com.java.dental_clinic.controller;

import com.java.dental_clinic.data.dto.ServiceDTO;
import com.java.dental_clinic.data.dto.TreatmentCreationDTO;
import com.java.dental_clinic.service.TreatmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/treatment")
public class TreatmentController {
    @Autowired
    private TreatmentService treatmentService;

    @GetMapping("")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "0") Long serviceId,
                              @RequestParam(defaultValue = "") String sortOrder) {

        return ResponseEntity.ok(treatmentService.filter(keyword, serviceId, sortOrder));
    }

    @GetMapping("/service")
    public ResponseEntity<?> findAll() {

        return ResponseEntity.ok(treatmentService.findAllService());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping("")
    public ResponseEntity<?> createTreatment(@RequestBody TreatmentCreationDTO treatmentCreationDTO) {

        return new ResponseEntity<>(treatmentService.createTreatment(treatmentCreationDTO), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTreatment(@RequestBody TreatmentCreationDTO treatmentCreationDTO,
                                             @PathVariable Long id) {

        return ResponseEntity.ok(treatmentService.updateTreatment(id, treatmentCreationDTO));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTreatment(@PathVariable Long id) {

        return ResponseEntity.ok(treatmentService.deleteTreatment(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping("/service")
    public ResponseEntity<?> createService(@RequestBody ServiceDTO serviceDTO) {

        return new ResponseEntity<>(treatmentService.createService(serviceDTO), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/service/{id}")
    public ResponseEntity<?> updateService(@RequestBody ServiceDTO serviceDTO, @PathVariable Long id) {

        return ResponseEntity.ok(treatmentService.updateService(serviceDTO, id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @DeleteMapping("/service/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {

        return ResponseEntity.ok(treatmentService.deleteService(id));
    }
}
