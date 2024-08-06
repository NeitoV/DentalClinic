package com.java.dental_clinic.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.java.dental_clinic.data.entity.Invoice;
import com.java.dental_clinic.repostiory.InvoiceRepository;
import com.java.dental_clinic.service.InvoiceService;
import com.java.dental_clinic.util.PDFUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
        Long objectiveId = Long.valueOf((String) creation.get("objectiveId"));
        String paymentMethod = (String) creation.get("paymentMethod");
        BigDecimal amountPaid = BigDecimal.valueOf(Long.parseLong((String) creation.get("amountPaid")));

        return new ResponseEntity<>(invoiceService.createInvoice(objectiveId, paymentMethod, amountPaid),
                HttpStatus.CREATED);
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
        BigDecimal paidDebit = BigDecimal.valueOf(Long.parseLong((String) update.get("paidDebit")));

        return ResponseEntity.ok(invoiceService.updateInvoice(id, paidDebit));
    }

//    @SecurityRequirement(name = "Bearer Authentication")
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/export-pdf/{objectiveId}")
    public ResponseEntity<ByteArrayResource> exportPdfInvoiceByObjective(@PathVariable Long objectiveId) throws IOException, DocumentException {
        ByteArrayResource resource = invoiceService.generatePdfInvoiceByObjective(objectiveId);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + objectiveId + ".pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
