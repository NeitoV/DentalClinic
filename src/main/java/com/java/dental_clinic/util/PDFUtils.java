package com.java.dental_clinic.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.java.dental_clinic.data.entity.Invoice;
import com.java.dental_clinic.data.entity.TherapyProcedure;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class PDFUtils {
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    public void addInvoiceContent(Document document, Invoice invoice, BaseFont baseFont,
                                  List<TherapyProcedure> list, String staffName) throws DocumentException {
        Paragraph introduce = new Paragraph("Nha Khoa Võ Tiến",
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        introduce.setAlignment(Element.ALIGN_LEFT);
        document.add(introduce);

        Paragraph heading = new Paragraph("Hoá đơn thanh toán",
                new com.itextpdf.text.Font(baseFont, 28, com.itextpdf.text.Font.BOLD));

        heading.setAlignment(Element.ALIGN_CENTER);
        document.add(heading);
        document.add(new Paragraph(" "));

        Paragraph username = new Paragraph("Tên khách hàng: " + invoice.getObjective().getMedicalRecord().getPatient().getName(),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        username.setAlignment(Element.ALIGN_LEFT);
        document.add(username);

        Paragraph address = new Paragraph("Địa chỉ: " + invoice.getObjective().getMedicalRecord().getPatient().getAddress(),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        address.setAlignment(Element.ALIGN_LEFT);
        document.add(address);

        Paragraph phoneNumber = new Paragraph("Số điện thoại: " +
                invoice.getObjective().getMedicalRecord().getPatient().getUser().getPhoneNumber(),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        address.setAlignment(Element.ALIGN_LEFT);
        document.add(phoneNumber);

        Paragraph diagnosis = new Paragraph("Chuẩn đoán chi tiết: " + invoice.getObjective().getDiagnosis(),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        address.setAlignment(Element.ALIGN_LEFT);
        document.add(diagnosis);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        BigDecimal totalProc = addProcedureTable(document, list, baseFont);

        document.add(new Paragraph(" "));
        Paragraph total = new Paragraph("Tổng cộng: " + numberFormat.format(totalProc),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        Paragraph paid = new Paragraph("Đã thanh toán: " + numberFormat.format(invoice.getAmountPaid()),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        paid.setAlignment(Element.ALIGN_RIGHT);
        document.add(paid);

        Paragraph remaining = new Paragraph("Còn lại: " + numberFormat.format(invoice.getAmountRemaining()),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        remaining.setAlignment(Element.ALIGN_RIGHT);
        document.add(remaining);

        document.add(new Paragraph(" "));
        addSignatureTable(document, baseFont, staffName);
    }

    public BaseFont loadBaseFont() throws DocumentException, IOException {

        return BaseFont.createFont("src/main/resources/fonts/arial-unicode-ms.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    private BigDecimal addProcedureTable(Document document, List<TherapyProcedure> list, BaseFont baseFont) throws DocumentException {
        PdfPTable table = new PdfPTable(4); // 4 columns
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 2, 2, 2});

        Font tableHeaderFont = new Font(baseFont, 14, Font.BOLD);
        Font tableBodyFont = new Font(baseFont, 12, Font.NORMAL);

        PdfPCell headerCell;

        headerCell = new PdfPCell(new Phrase("Phương thức điều trị", tableHeaderFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setMinimumHeight(30f);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(headerCell);

        headerCell = new PdfPCell(new Phrase("Số lượng", tableHeaderFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setMinimumHeight(30f);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(headerCell);

        headerCell = new PdfPCell(new Phrase("Đơn giá", tableHeaderFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setMinimumHeight(30f);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(headerCell);

        headerCell = new PdfPCell(new Phrase("Thành tiền", tableHeaderFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setMinimumHeight(30f);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(headerCell);

        BigDecimal total = new BigDecimal(0);

        for (TherapyProcedure procedure : list) {
            BigDecimal countBigDecimal = BigDecimal.valueOf(procedure.getCount());
            BigDecimal procedureCost = procedure.getTreatment().getCost().multiply(countBigDecimal);
            total = total.add(procedureCost);

            PdfPCell bodyCell;

            bodyCell = new PdfPCell(new Phrase(procedure.getTreatment().getName(), tableBodyFont));
            bodyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            bodyCell.setMinimumHeight(30f); // Set minimum height for body cells
            bodyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(bodyCell);

            bodyCell = new PdfPCell(new Phrase(numberFormat.format(procedure.getCount()), tableBodyFont));
            bodyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bodyCell.setMinimumHeight(30f); // Set minimum height for body cells
            bodyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(bodyCell);

            bodyCell = new PdfPCell(new Phrase(numberFormat.format(procedure.getTreatment().getCost()), tableBodyFont));
            bodyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bodyCell.setMinimumHeight(30f); // Set minimum height for body cells
            bodyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(bodyCell);

            bodyCell = new PdfPCell(new Phrase(numberFormat.format(procedureCost), tableBodyFont));
            bodyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bodyCell.setMinimumHeight(30f); // Set minimum height for body cells
            bodyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(bodyCell);
        }

        document.add(table);

        return total;
    }

    private void addSignatureTable(Document document, BaseFont baseFont, String staff) throws DocumentException {
        PdfPTable table = new PdfPTable(2); // 2 columns for signatures
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1});

        Font signatureFont = new Font(baseFont, 16, Font.BOLD);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Ngày' d 'tháng' M 'năm' yyyy",
                new Locale("vi", "VN"));

        PdfPCell emtyCell = new PdfPCell(new Phrase(" ", signatureFont));
        emtyCell.setBorder(Rectangle.NO_BORDER);
        emtyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(emtyCell);

        PdfPCell date = new PdfPCell(new Phrase("HCM, " + LocalDate.now().format(formatter), signatureFont));
        date.setBorder(Rectangle.NO_BORDER);
        date.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(date);

        PdfPCell staffCell = new PdfPCell(new Phrase("     Bác sĩ", signatureFont));
        staffCell.setBorder(Rectangle.NO_BORDER);
        staffCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        staffCell.setPaddingBottom(100f);
        table.addCell(staffCell);

        PdfPCell patientCell = new PdfPCell(new Phrase("Bệnh nhân", signatureFont));
        patientCell.setBorder(Rectangle.NO_BORDER);
        patientCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(patientCell);

        PdfPCell staffName = new PdfPCell(new Phrase(staff, signatureFont));
        staffName.setBorder(Rectangle.NO_BORDER);
        staffName.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(staffName);

        PdfPCell patientName = new PdfPCell(new Phrase(" ", signatureFont));
        patientName.setBorder(Rectangle.NO_BORDER);
        patientName.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(patientName);

        document.add(table);
    }

}
