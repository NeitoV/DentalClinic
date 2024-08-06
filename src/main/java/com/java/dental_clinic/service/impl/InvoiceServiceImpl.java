package com.java.dental_clinic.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.java.dental_clinic.data.dto.DentistRevenueDTO;
import com.java.dental_clinic.data.dto.InvoiceDTO;
import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.entity.*;
import com.java.dental_clinic.data.maper.InvoiceMapper;
import com.java.dental_clinic.data.maper.StaffMapper;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.*;
import com.java.dental_clinic.service.CalendarWorkingService;
import com.java.dental_clinic.service.InvoiceService;
import com.java.dental_clinic.service.MailService;
import com.java.dental_clinic.util.PDFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private ProcedureRepository procedureRepository;
    @Autowired
    private ObjectiveRepository objectiveRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceMapper invoiceMapper;
    @Autowired
    private CalendarWorkingService calendarWorkingService;
    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private PDFUtils pdfUtils;

    @Override
    public BigDecimal getTotalAmountByObjective(Long objectiveId) {
        BigDecimal total = new BigDecimal(0);

        List<TherapyProcedure> procedureList = procedureRepository.findAllByObjectiveId(objectiveId);
        for (TherapyProcedure therapyProcedure : procedureList) {

            BigDecimal countBigDecimal = BigDecimal.valueOf(therapyProcedure.getCount());
            BigDecimal procedureCost = therapyProcedure.getTreatment().getCost().multiply(countBigDecimal);

            total = total.add(procedureCost);
        }

        return total;
    }

    @Override
    public void exportInvoice(Long medicalRecordId) {
        MedicalRecord medicalRecord = recordRepository.findById(medicalRecordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("medical record id", medicalRecordId))
        );
        String fileName = medicalRecord.getPatient().getName() + "_" + medicalRecord.getDiagnosis() + ".pdf";
    }

    @Override
    public MessageResponse createInvoice(Long objectiveId, String paymentMethod, BigDecimal amountPaid) {
        Objective objective = objectiveRepository.findById(objectiveId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id:", objectiveId))
        );

        BigDecimal totalAmount = getTotalAmountByObjective(objectiveId);

        Invoice invoice = new Invoice();

        invoice.setDate(LocalDate.now());
        invoice.setPaymentMethod(paymentMethod);
        invoice.setAmountPaid(amountPaid);
        invoice.setAmountRemaining(totalAmount.subtract(amountPaid));
        invoice.setObjective(objective);

        invoiceRepository.save(invoice);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public InvoiceDTO getInvoiceByObjectiveId(Long objectiveId) {
        Invoice invoice = invoiceRepository.findByObjectiveId(objectiveId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("objectiveId:", objectiveId))
        );

        return invoiceMapper.toDTO(invoice);
    }

    @Override
    public DentistRevenueDTO getTotalRevenueByWeek(int year, int week, Long staffId) {
        LocalDate[] dates = calendarWorkingService.getStartAndEndDate(year, week);
        LocalDate startDate = dates[0];
        LocalDate endDate = dates[1];

        List<Object[]> list = invoiceRepository.findTotalAmountByDateRange(startDate, endDate, staffId);

        List<Map<String, Object>> revenues = invoiceRepository
                .findTotalAmountByDateRange(startDate, endDate, staffId)
                .stream()
                .map(objects -> Map.of("date", objects[0],
                        "totalRevenue", objects[1],
                        "totalAmountRemaining", objects[2]))
                .collect(Collectors.toList());

        DentistRevenueDTO dentistRevenueDTO = getRevenue(staffId);
        dentistRevenueDTO.setRevenues(revenues);

        return dentistRevenueDTO;
    }

    @Override
    public DentistRevenueDTO getTotalRevenueByMonth(int year, int month, Long staffId) {

        List<Object[]> list = invoiceRepository.findTotalRevenueByWeekInMonth(year, month, staffId);

        List<Map<String, Object>> revenues = list.stream()
                .map(objects -> Map.of("week", objects[0],
                        "totalRevenue", objects[1],
                        "totalAmountRemaining", objects[2]))
                .collect(Collectors.toList());

        DentistRevenueDTO dentistRevenueDTO = getRevenue(staffId);
        dentistRevenueDTO.setRevenues(revenues);

        return dentistRevenueDTO;
    }

    @Override
    public DentistRevenueDTO getTotalRevenueByYear(int year, Long staffId) {

        DentistRevenueDTO dentistRevenueDTO = getRevenue(staffId);
        List<Map<String, Object>> revenues = invoiceRepository.findTotalRevenueByMonthInYear(year, staffId)
                .stream()
                .map(objects -> Map.of("month", objects[0],
                        "totalRevenue", objects[1],
                        "totalAmountRemaining", objects[2]))
                .collect(Collectors.toList());

        dentistRevenueDTO.setRevenues(revenues);

        return dentistRevenueDTO;
    }

    @Override
    public MessageResponse updateInvoice(Long invoiceId, BigDecimal paidDebit) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("invoiceId:", invoiceId)));

        invoice.setAmountPaid(invoice.getAmountPaid().add(paidDebit));
        invoice.setAmountRemaining(invoice.getAmountRemaining().subtract(paidDebit));
        invoiceRepository.save(invoice);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public ByteArrayResource generatePdfInvoiceByObjective(Long objectiveId) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Objective objective = objectiveRepository.findById(objectiveId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("objectiveId:", objectiveId))
        );
        List<TherapyProcedure> list = procedureRepository.findAllByObjectiveId(objectiveId);

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();
            BaseFont baseFont = pdfUtils.loadBaseFont();

            pdfUtils.addInvoiceContent(document, objective, baseFont, list);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayResource(out.toByteArray());
    }

    private DentistRevenueDTO getRevenue(Long staffId) {
        DentistRevenueDTO dentistRevenueDTO = new DentistRevenueDTO();

        Staff staff = staffRepository.findById(staffId).orElse(null);
        dentistRevenueDTO.setStaffDTO(staffMapper.toDTO(staff));

        return dentistRevenueDTO;
    }

}
