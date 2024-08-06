package com.java.dental_clinic.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.java.dental_clinic.data.entity.Objective;
import com.java.dental_clinic.data.entity.TherapyProcedure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PDFUtils {

    public void addInvoiceContent(Document document, Objective objective, BaseFont baseFont,
                                  List<TherapyProcedure> list) throws DocumentException {

        Paragraph heading = new Paragraph("Hoá đơn thanh toán",
                new com.itextpdf.text.Font(baseFont, 28, com.itextpdf.text.Font.BOLD));

        heading.setAlignment(Element.ALIGN_CENTER);
        document.add(heading);
        document.add(new Paragraph(" "));

        Paragraph username = new Paragraph("Tên khách hàng: " + objective.getMedicalRecord().getPatient().getName(),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        username.setAlignment(Element.ALIGN_LEFT);
        document.add(username);

        Paragraph address = new Paragraph("Địa chỉ: " + objective.getMedicalRecord().getPatient().getAddress(),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        address.setAlignment(Element.ALIGN_LEFT);
        document.add(address);

        Paragraph phoneNumber = new Paragraph("Số điện thoại: " +
                objective.getMedicalRecord().getPatient().getUser().getPhoneNumber(),
                new com.itextpdf.text.Font(baseFont, 16, com.itextpdf.text.Font.NORMAL));
        document.add(phoneNumber);
    }

    public BaseFont loadBaseFont() throws DocumentException, IOException {

        return BaseFont.createFont("src/main/resources/fonts/arial-unicode-ms.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    private void addProcedureTable(Document document, Objective objective, BaseFont baseFont) throws DocumentException {
        PdfPTable table = new PdfPTable(3); // 3 columns
        table.setWidthPercentage(100);

        Font tableHeaderFont = new Font(baseFont, 14, Font.BOLD);
        Font tableBodyFont = new Font(baseFont, 12, Font.NORMAL);

        table.addCell(new PdfPCell(new Phrase("Procedure Name", tableHeaderFont)));
        table.addCell(new PdfPCell(new Phrase("Description", tableHeaderFont)));
        table.addCell(new PdfPCell(new Phrase("Cost", tableHeaderFont)));


        document.add(table);
    }

}
