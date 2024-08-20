package com.java.dental_clinic.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private Long id;
    private LocalDate date;
    private String paymentMethod;
    private ObjectiveDTO objectiveDTO;
    private BigDecimal amountPaid;
    private BigDecimal amountRemaining;
}
