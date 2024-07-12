package com.java.dental_clinic.service;

import java.math.BigDecimal;

public interface InvoiceService {
    BigDecimal getTotalAmountMedicalRecord(Long medicalRecordId);

    void exportInvoice(Long medicalRecordId);
}
