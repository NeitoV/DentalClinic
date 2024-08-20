package com.java.dental_clinic.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.java.dental_clinic.data.entity.Invoice;
import com.java.dental_clinic.data.entity.Objective;
import com.java.dental_clinic.data.entity.TherapyProcedure;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class PDFUtils {
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public void addInvoiceContent(Document document, Invoice invoice, BaseFont baseFont,
                                  List<TherapyProcedure> list) throws DocumentException {
        String introduceRight = "Mã hoá đơn: " + invoice.getId();
        addClinicIntroduction(document, baseFont, introduceRight);

        addHeading(document, baseFont, "Hoá đơn thanh toán");

        addPatientInformation(document, invoice.getObjective(), baseFont);
        document.add(new Paragraph(" "));

        BigDecimal totalProc = addProcedureTable(document, list, baseFont);
        document.add(new Paragraph(" "));

        addTotals(document, totalProc, invoice, baseFont);
        document.add(new Paragraph(" "));

        addSignatureTable(document, baseFont, invoice.getObjective().getMedicalRecord().getStaff().getName());
    }

    public void addObjectiveContent(Document document, BaseFont baseFont, List<TherapyProcedure> procedureList)
            throws DocumentException {
        String introRight = "Mã điều trị: " + procedureList.get(0).getObjective().getId();
        addClinicIntroduction(document, baseFont, introRight);

        addHeading(document, baseFont, "Kết quả điều trị");

        addPatientInformation(document, procedureList.get(0).getObjective(), baseFont);
        document.add(new Paragraph(" "));

        BigDecimal totalProc = addProcedureTable(document, procedureList, baseFont);
        document.add(new Paragraph(" "));

        addTotalRow(document, "Tổng cộng: ", totalProc, baseFont);
        document.add(new Paragraph(" "));

        addSignatureTable(document, baseFont, procedureList.get(0).getObjective().getMedicalRecord().getStaff().getName());
    }


    private void addClinicIntroduction(Document document, BaseFont baseFont, String introduceRight) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{6, 2});

        Font headerFont = new Font(baseFont, 16, Font.NORMAL);

        // Clinic Name
        PdfPCell clinicNameCell = new PdfPCell(new Phrase("Nha Khoa Võ Tiến", headerFont));
        clinicNameCell.setBorder(Rectangle.NO_BORDER);
        clinicNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerTable.addCell(clinicNameCell);

        // Introduce Right
        PdfPCell invoiceIdCell = new PdfPCell(new Phrase(introduceRight, headerFont));
        invoiceIdCell.setBorder(Rectangle.NO_BORDER);
        invoiceIdCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        headerTable.addCell(invoiceIdCell);

        document.add(headerTable);
    }


    private void addHeading(Document document, BaseFont baseFont, String header) throws DocumentException {
        Paragraph heading = new Paragraph(header,
                new Font(baseFont, 28, Font.BOLD));
        heading.setAlignment(Element.ALIGN_CENTER);
        document.add(heading);
        document.add(new Paragraph(" "));
    }

    private void addPatientInformation(Document document, Objective objective, BaseFont baseFont)
            throws DocumentException {

        Paragraph username = new Paragraph("Tên khách hàng: " + objective.getMedicalRecord().getPatient().getName(),
                new Font(baseFont, 16, Font.NORMAL));
        username.setAlignment(Element.ALIGN_LEFT);
        document.add(username);

        Paragraph address = new Paragraph("Địa chỉ: " + objective.getMedicalRecord().getPatient().getAddress(),
                new Font(baseFont, 16, Font.NORMAL));
        address.setAlignment(Element.ALIGN_LEFT);
        document.add(address);

        Paragraph phoneNumber = new Paragraph("Số điện thoại: " +
                objective.getMedicalRecord().getPatient().getUser().getPhoneNumber(),
                new Font(baseFont, 16, Font.NORMAL));

        phoneNumber.setAlignment(Element.ALIGN_LEFT);
        document.add(phoneNumber);

        Paragraph diagnosis = new Paragraph("Chuẩn đoán chi tiết: " + objective.getDiagnosis(),
                new Font(baseFont, 16, Font.NORMAL));
        diagnosis.setAlignment(Element.ALIGN_LEFT);
        document.add(diagnosis);
    }

    private BigDecimal addProcedureTable(Document document, List<TherapyProcedure> list, BaseFont baseFont)
            throws DocumentException {

        PdfPTable table = createProcedureTable(baseFont);

        BigDecimal total = BigDecimal.ZERO;
        for (TherapyProcedure procedure : list) {
            total = total.add(addProcedureRow(table, procedure, baseFont));
        }

        document.add(table);
        return total;
    }

    private PdfPTable createProcedureTable(BaseFont baseFont) throws DocumentException {
        PdfPTable table = new PdfPTable(4); // 4 columns
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 2, 2, 2});

        Font tableHeaderFont = new Font(baseFont, 14, Font.BOLD);
        addTableHeader(table, "Phương thức điều trị", tableHeaderFont);
        addTableHeader(table, "Số lượng", tableHeaderFont);
        addTableHeader(table, "Đơn giá", tableHeaderFont);
        addTableHeader(table, "Thành tiền", tableHeaderFont);

        return table;
    }

    private void addTableHeader(PdfPTable table, String headerText, Font font) {
        PdfPCell headerCell = new PdfPCell(new Phrase(headerText, font));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setMinimumHeight(30f);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(headerCell);
    }

    private BigDecimal addProcedureRow(PdfPTable table, TherapyProcedure procedure, BaseFont baseFont) {
        Font tableBodyFont = new Font(baseFont, 12, Font.NORMAL);

        BigDecimal countBigDecimal = BigDecimal.valueOf(procedure.getCount());
        BigDecimal procedureCost = procedure.getTreatment().getCost().multiply(countBigDecimal);

        addTableCell(table, procedure.getTreatment().getName(), tableBodyFont, Element.ALIGN_LEFT);
        addTableCell(table, numberFormat.format(procedure.getCount()), tableBodyFont, Element.ALIGN_CENTER);
        addTableCell(table, numberFormat.format(procedure.getTreatment().getCost()), tableBodyFont, Element.ALIGN_CENTER);
        addTableCell(table, numberFormat.format(procedureCost), tableBodyFont, Element.ALIGN_CENTER);

        return procedureCost;
    }

    private void addTableCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell bodyCell = new PdfPCell(new Phrase(text, font));
        bodyCell.setHorizontalAlignment(alignment);
        bodyCell.setMinimumHeight(30f); // Set minimum height for body cells
        bodyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(bodyCell);
    }

    private void addTotals(Document document, BigDecimal totalProc, Invoice invoice, BaseFont baseFont)
            throws DocumentException {

        addTotalRow(document, "Tổng cộng: ", totalProc, baseFont);
        addTotalRow(document, "Đã thanh toán: ", invoice.getAmountPaid(), baseFont);
        addTotalRow(document, "Còn lại: ", invoice.getAmountRemaining(), baseFont);
    }

    private void addTotalRow(Document document, String label, BigDecimal amount, BaseFont baseFont)
            throws DocumentException {

        Paragraph total = new Paragraph(label + numberFormat.format(amount),
                new Font(baseFont, 16, Font.NORMAL));
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
    }

    public BaseFont loadBaseFont() throws DocumentException, IOException {
        InputStream fontStream = getClass().getResourceAsStream("/fonts/arial-unicode-ms.ttf");

        if (fontStream == null) {
            throw new AccessDeniedException("Cannot load font file");
        }

        try {
            return BaseFont.createFont("arial-unicode-ms.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED,
                    true, fontStream.readAllBytes(), null);
        } finally {
            fontStream.close();
        }
    }

    private void addSignatureTable(Document document, BaseFont baseFont, String staff) throws DocumentException {
        PdfPTable table = new PdfPTable(2); // 2 columns for signatures
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1}); // Adjust these values if needed

        Font signatureFont = new Font(baseFont, 16, Font.BOLD);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Ngày' d 'tháng' M 'năm' yyyy",
                new Locale("vi", "VN"));

        PdfPCell emptyCell = new PdfPCell(new Phrase(" ", signatureFont));
        emptyCell.setBorder(Rectangle.NO_BORDER);
        emptyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(emptyCell);

        PdfPCell date = new PdfPCell(new Phrase("HCM, " + LocalDate.now().format(formatter), signatureFont));
        date.setBorder(Rectangle.NO_BORDER);
        date.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(date);

        // Patient's cell (aligned left)
        PdfPCell patientCell = new PdfPCell(new Phrase("Bệnh nhân", signatureFont));
        patientCell.setBorder(Rectangle.NO_BORDER);
        patientCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        patientCell.setPaddingBottom(50f);
        patientCell.setPaddingLeft(20f);
        table.addCell(patientCell);

        // Doctor's cell (aligned right)
        PdfPCell staffCell = new PdfPCell();
        staffCell.setBorder(Rectangle.NO_BORDER);
        staffCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        // Add a nested table for "Bác sĩ" and the doctor's name
        PdfPTable nestedTable = new PdfPTable(1);
        nestedTable.setWidthPercentage(100);

        // Bác sĩ label
        PdfPCell labelCell = new PdfPCell(new Phrase("Bác sĩ", signatureFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPaddingBottom(100f);
        labelCell.setPaddingRight(50f);
        nestedTable.addCell(labelCell);

        // Doctor's name
        PdfPCell nameCell = new PdfPCell(new Phrase(staff, signatureFont));
        nameCell.setBorder(Rectangle.NO_BORDER);
        nameCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        nameCell.setPaddingRight(20f);
        nestedTable.addCell(nameCell);

        staffCell.addElement(nestedTable);
        table.addCell(staffCell);


        document.add(table);
    }




}
