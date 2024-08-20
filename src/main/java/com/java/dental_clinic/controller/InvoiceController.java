package com.java.dental_clinic.controller;

import com.itextpdf.text.DocumentException;
import com.java.dental_clinic.data.entity.Invoice;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.exception.ValidationException;
import com.java.dental_clinic.repostiory.InvoiceRepository;
import com.java.dental_clinic.service.InvoiceService;
import com.java.dental_clinic.util.PDFUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private PDFUtils pdfUtils;
    @Autowired
    private InvoiceRepository invoiceRepository;

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @PostMapping("")
    ResponseEntity<?> creatInvoice(@RequestBody Map<String, Object> creation) {
        try {
            Long objectiveId = Long.valueOf(creation.get("objectiveId").toString());
            String paymentMethod = (String) creation.get("paymentMethod");
            BigDecimal amountPaid = BigDecimal.valueOf(Long.parseLong(creation.get("amountPaid").toString()));

            return new ResponseEntity<>(invoiceService.createInvoice(objectiveId, paymentMethod, amountPaid),
                    HttpStatus.CREATED);

        } catch (Exception e) {
            throw new ResourceNotFoundException(Collections.singletonMap("objectiveId", creation.get("objectiveId")));
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/objective/{id}")
    ResponseEntity<?> getInvoiceByObjective(@PathVariable Long id) {

        return ResponseEntity.ok(invoiceService.getInvoiceByObjectiveId(id));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @GetMapping("/revenue/week")
    ResponseEntity<?> getRevenueByWeek(@RequestParam() int year,
                                       @RequestParam() int week,
                                       @RequestParam(defaultValue = "0") Long staffId) {

        return ResponseEntity.ok(invoiceService.getTotalRevenueByWeek(year, week, staffId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @GetMapping("/revenue/month")
    ResponseEntity<?> getRevenueByMonth(@RequestParam() int year,
                                        @RequestParam() int month,
                                        @RequestParam(defaultValue = "0") Long staffId) {

        return ResponseEntity.ok(invoiceService.getTotalRevenueByMonth(year, month, staffId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @GetMapping("/revenue/year")
    ResponseEntity<?> getRevenueByYear(@RequestParam() int year,
                                       @RequestParam(defaultValue = "0") Long staffId) {

        return ResponseEntity.ok(invoiceService.getTotalRevenueByYear(year, staffId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @PutMapping("/{id}")
    ResponseEntity<?> updateInvoice(@PathVariable Long id, @RequestBody Map<String, Object> update) {
        BigDecimal paidDebit = BigDecimal.valueOf(Long.parseLong(update.get("paidDebit").toString()));

        return ResponseEntity.ok(invoiceService.updateInvoice(id, paidDebit));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/export-pdf/{invoiceId}")
    public ResponseEntity<ByteArrayResource> exportPdfInvoiceByObjective(@PathVariable Long invoiceId)
            throws IOException, DocumentException {

        HttpHeaders headers = new HttpHeaders();
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("invoiceId:", invoiceId))
        );
        ByteArrayResource resource = invoiceService.generatePdfInvoiceByObjective(invoice);
        String patient = invoice.getObjective().getMedicalRecord().getPatient().getName();

        String fileName = "HD" + invoiceId + ".pdf";

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyAuthority('Role_Admin', 'Role_Staff')")
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long invoiceId) {

        return ResponseEntity.ok(invoiceService.deleteInvoice(invoiceId));
    }
}
