package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.DentistRevenueDTO;
import com.java.dental_clinic.data.dto.InvoiceDTO;
import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.entity.Invoice;
import org.springframework.core.io.ByteArrayResource;

import java.math.BigDecimal;


public interface InvoiceService {
    BigDecimal getTotalAmountByObjective(Long objectiveId);


    MessageResponse createInvoice(Long objectiveId, String paymentMethod, BigDecimal amountPaid);

    InvoiceDTO getInvoiceByObjectiveId(Long objectiveId);

    DentistRevenueDTO getTotalRevenueByWeek(int year, int week, Long staffId);

    DentistRevenueDTO getTotalRevenueByMonth(int year, int month, Long staffId);

    DentistRevenueDTO getTotalRevenueByYear(int year, Long staffId);

    MessageResponse updateInvoice(Long invoiceId, BigDecimal paidDebit);

    ByteArrayResource generatePdfInvoiceByObjective(Invoice invoice);

    MessageResponse deleteInvoice(Long invoiceId);
}
