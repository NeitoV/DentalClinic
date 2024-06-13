package com.java.dental_clinic.controller;

import com.java.dental_clinic.data.dto.staff.StaffCreationDTO;
import com.java.dental_clinic.data.dto.staff.StaffDTO;
import com.java.dental_clinic.data.entity.Schedule;
import com.java.dental_clinic.service.ScheduleService;
import com.java.dental_clinic.service.StaffService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/staff")
public class StaffController {
    @Autowired
    private StaffService staffService;
    @Autowired
    private ScheduleService scheduleService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PostMapping("")
    public ResponseEntity<?> createStaff(@RequestBody StaffCreationDTO staffCreationDTO) {

        return new ResponseEntity<>(staffService.createStaff(staffCreationDTO), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@RequestBody StaffDTO staffDTO, @PathVariable Long id) {

        return ResponseEntity.ok(staffService.updateStaff(staffDTO, id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filter")
    public ResponseEntity<?> filterStaff(@RequestParam(defaultValue = "0") int pageNumber,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(defaultValue = "") String keyword,
                                         @RequestParam(defaultValue = "0") Long positionId) {

        return ResponseEntity.ok(staffService.filterStaff(keyword, pageNumber, pageSize, positionId));
    }

}
