package com.yogi.account.Account.pdfManipulation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PdfBox {

    public String read(PDDocument document,int pageNumber, Boolean sortByPosition) {
        try {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            if (sortByPosition){
                pdfStripper.setSortByPosition(true);
            }
            pdfStripper.setStartPage(pageNumber);
            pdfStripper.setEndPage(pageNumber);
            String text = pdfStripper.getText(document);
            document.close();
            return text;
        } catch (RuntimeException | IOException e) {
            System.out.println("Exception occured"  + e.getMessage());
           return "Exception occured"  + e.getMessage();
        }
    }



}
