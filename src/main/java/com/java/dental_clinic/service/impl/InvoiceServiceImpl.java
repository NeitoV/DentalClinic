package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.entity.MedicalRecord;
import com.java.dental_clinic.data.entity.TherapyProcedure;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.ProcedureRepository;
import com.java.dental_clinic.repostiory.RecordRepository;
import com.java.dental_clinic.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private ProcedureRepository procedureRepository;


    @Override
    public BigDecimal getTotalAmountMedicalRecord(Long medicalRecordId) {
        MedicalRecord medicalRecord = recordRepository.findById(medicalRecordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("medical record id", medicalRecordId))
        );

        BigDecimal total = BigDecimal.ZERO;
        List<TherapyProcedure> procedureList = procedureRepository.findAllByMedicalRecordId(medicalRecordId);
        for(TherapyProcedure procedure: procedureList) {
            BigDecimal countBigdecimal = BigDecimal.valueOf(procedure.getCount());
            BigDecimal procedureAmount = procedure.getTreatment().getCost().multiply(countBigdecimal);

            total = total.add(procedureAmount);
        }

        return total;
    }

    @Override
    public void exportInvoice(Long medicalRecordId) {
        MedicalRecord medicalRecord = recordRepository.findById(medicalRecordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("medical record id", medicalRecordId))
        );
        String fileName = medicalRecord.getPatient().getName() +"_"+ medicalRecord.getDiagnosis() +".pdf";
    }
}
