package com.example.financehelp;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFReader {

    public static String readPDF(File pdfFile) {
        StringBuilder text = new StringBuilder();
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            text.append(stripper.getText(document));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}
