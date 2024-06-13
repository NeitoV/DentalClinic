package com.java.dental_clinic.controller;

import com.java.dental_clinic.service.TreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

}
