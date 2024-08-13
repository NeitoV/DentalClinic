package com.java.dental_clinic.controller;

import com.java.dental_clinic.data.dto.ObjectivesCreationDTO;
import com.java.dental_clinic.service.ObjectiveService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/objective")
public class ObjectiveController {
    @Autowired
    private ObjectiveService objectiveService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Staff')")
    @PostMapping("/record/{id}")
    public ResponseEntity<?> createObjectiveForRecord(@PathVariable Long id,
                                                      @RequestBody List<ObjectivesCreationDTO> list) {

        return new ResponseEntity<>(objectiveService.createObjectiveForRecord(id, list), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Staff')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteObjective(@PathVariable Long id) {

        return ResponseEntity.ok(objectiveService.deleteObjective(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/record/{id}")
    public ResponseEntity<?> findAllByRecord(@PathVariable Long id,
                                             @RequestParam(defaultValue = "0") int pageNumber,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(required = false) Long objectiveId) {

        return ResponseEntity.ok(objectiveService.findAllByRecordId(id, pageNumber, pageSize, objectiveId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    ResponseEntity<?> findById(@PathVariable Long id) {

        return ResponseEntity.ok(objectiveService.findById(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pdf/{id}")
    public  ResponseEntity<?> createPdfObjective(@PathVariable Long id) {

        HttpHeaders headers = new HttpHeaders();
        ByteArrayResource resource = objectiveService.createPdfObjective(id);

        String fileName = "KQDT" + id + ".pdf";

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
