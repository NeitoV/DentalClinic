package com.java.dental_clinic.controller;

import com.itextpdf.text.DocumentException;
import com.java.dental_clinic.service.InvoiceService;
import com.java.dental_clinic.util.PDFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private PDFUtils pdfUtils;

    @GetMapping("/record/{recordId}")
    ResponseEntity<?> getAmountByMedicalRecordId(@PathVariable Long recordId) {

        return ResponseEntity.ok(invoiceService.getTotalAmountMedicalRecord(recordId));
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<ByteArrayResource> exportPdf() throws IOException, DocumentException {
        try {
            ByteArrayOutputStream out = pdfUtils.export();
            ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + System.currentTimeMillis() + ".pdf");
            headers.setContentType(MediaType.APPLICATION_PDF);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
