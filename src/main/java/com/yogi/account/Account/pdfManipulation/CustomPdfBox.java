package com.yogi.account.Account.pdfManipulation;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
@AllArgsConstructor
public class CustomPdfBox extends PDFTextStripper {
    private Rectangle area;
    private StringBuilder extractedText;

    private java.util.List<java.util.List<Object>> data;

    public  CustomPdfBox() throws IOException {
        super();
        extractedText = new StringBuilder();
        data = new ArrayList<>();
    }

    public void setArea(Rectangle area) {
        this.area = area;
    }

    @Override
    protected void writeString(String string, java.util.List<TextPosition> textPositions) {
        // Check if any part of the text falls within the specified area

        for (TextPosition text : textPositions) {
            java.util.List<Object> row = new ArrayList<>();
            row.add(string);
            row.add(text.getXDirAdj());
            row.add(text.getYDirAdj());

            data.add(row);
        }
    }


    public List<List<Object>> getdatalist() {
        return data;
    }
    
    

    public  List<String>  fetchDocumentText (PDDocument document,int startPage,int sendPage) throws IOException {
        extractedText.setLength(0); // Clear the StringBuilder before each use
        super.setSortByPosition(true);
        super.setStartPage(startPage);
        super.setEndPage(sendPage);
        super.getText(document);
        List<List<Object>> words = getdatalist();
        List<String> documentText = new ArrayList<>();
        String line = "";
        float ylocPrevious  = 0;
        float xlocPrevious  = 0;
        String previousword = "";
        for (int i = 0;i<words.size();i++){
          List<Object>  wordData = words.get(i);
           String word = wordData.get(0).toString();
           float xloc = (float) wordData.get(1);
            float yloc = (float) wordData.get(2);
            if (ylocPrevious ==0 || xlocPrevious == 0 || StringUtils.isBlank(previousword)) {
                ylocPrevious = yloc;
                xlocPrevious = xloc;
                previousword = word;
                line =  StringUtils.join(line , "", word);
            }
            if (yloc == ylocPrevious  ){
                if (word != previousword && xloc-xlocPrevious<1   ){
                    line =  StringUtils.join(line , "", word);
                    previousword = word;
                }
                else if(word != previousword && xloc-xlocPrevious>10  ){
                    line = StringUtils.join(line, "\n ", word);
                    previousword = word;
                    xlocPrevious = xloc;
                }
            }
            if (yloc != ylocPrevious ){
                if (yloc-ylocPrevious<10 && word != previousword ){
                  String oldword = (String) words.stream().filter(y ->yloc-(float) y.get(2)<10).filter(key ->(float) key.get(1)-xloc>=10).filter(key->(float) key.get(1)-xloc>=10).findFirst().get().get(0);
                    ylocPrevious =yloc;
                    xlocPrevious = xloc;
                    previousword=word;
                    line = StringUtils.join(line, "\n ", word);
                }
                if (yloc-ylocPrevious>10) {
                    if (!StringUtils.isBlank(line)) {
                        documentText.add(line);
                    }
                    
                    ylocPrevious = yloc;
                    xlocPrevious = xloc;
                    line = word;
                    previousword = word;
                }
            }
        }
        return documentText;
    }
}
