package com.java.dental_clinic.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PDFUtils {
    public ByteArrayOutputStream export() throws IOException, DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, BaseColor.BLACK);
        document.add(new Paragraph("Hello, World!", font));
        document.close();

        return out;
    }
}
