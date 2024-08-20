package com.java.dental_clinic.controller;

import com.java.dental_clinic.data.dto.WorkingDTO;
import com.java.dental_clinic.service.CalendarWorkingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/working")
public class WorkingController {
    @Autowired
    private CalendarWorkingService calendarWorkingService;

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Staff')")
    @PostMapping("")
    ResponseEntity<?> createWorkingCalendar(@RequestBody Map<String, List<WorkingDTO>> list) {
        List<WorkingDTO> workingDTOList = list.get("working calendar");

        return new ResponseEntity<>(calendarWorkingService.createWorkingCalendar(workingDTOList), HttpStatus.CREATED);
    }

    @GetMapping("")
    ResponseEntity<?> filter(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @RequestParam(defaultValue = "0") Long periodId,
                             @RequestParam(defaultValue = "") String keyword) {

        return ResponseEntity.ok(calendarWorkingService.filter(keyword, date, periodId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Staff')")
    @DeleteMapping("/{date}/{periodId}")
    ResponseEntity<?> deleteCalendarWorking(@PathVariable Long periodId,
                                            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(calendarWorkingService.deleteWorkingCalendar(periodId, date));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('Role_Staff')")
    @GetMapping("/{week}/{year}")
    ResponseEntity<?> findByWeek(@PathVariable int week, @PathVariable int year) {

        return ResponseEntity.ok(calendarWorkingService.findByWeek(year, week));
    }
}
