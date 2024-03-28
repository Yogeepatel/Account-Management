package com.yogi.account.Account.pdfManipulation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.util.ArrayList;
import java.util.List;

@Component
public class Tabula {

    SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
    public List<List<String>> pdfPartialTableReader(PDDocument pd , int pageNumber ) {

      List<List<String>> data  = new ArrayList<>();
        PageIterator pi = new ObjectExtractor(pd).extract();


        int i = 0;
        while (pi.hasNext()) {
            i = i + 1;
            Page page = pi.next();
            if (i == pageNumber) {
                List<Ruling> horizontalRulings = page.getHorizontalRulings();
                int horizontalRulingsSize = horizontalRulings.size();
                List<Table> tables = sea.extract(page);
                Table t = tables.get(0);
                System.out.println(tables);
                page.addRuling(new Ruling(t.getTop(), (float) horizontalRulings.get(0).getX1(), 0, t.height));
                page.addRuling(new Ruling(t.getTop(), (float) horizontalRulings.get(0).getX2(), 0, t.height));
                page.addRuling(new Ruling(281f, 32.769650f, 533.48035f, 0));
                List<Table> newtables = sea.extract(page);
                for (Table table : newtables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();
                    for (List<RectangularTextContainer> cells : rows) {
                        List<String> row = new ArrayList<>();
                        for (RectangularTextContainer content : cells) {
                            String text = content.getText();
                            row.add(text);
                        }
                        data.add(row);
                    }
                }
            }
        }
        return data;
    }

    public List<List<String>> pdfTableReader(PDDocument pd , int pageNumber) {
        Page page = new ObjectExtractor(pd).extract(pageNumber);
        List<List<String>> data  = new ArrayList<>();

            List<Table> tables = sea.extract(page);
        System.out.println(tables.size());
            for (Table table : tables) {
                List<List<RectangularTextContainer>> rows = table.getRows();
                for (List<RectangularTextContainer> cells : rows) {
                    List<String> row = new ArrayList<>();
                    for (RectangularTextContainer content : cells) {
                        String text = content.getText(false);
                        row.add(text);
                    }
                    data.add(row);
                }

            }
        return data;
    }

    public List<List<String>> pdfTableReaderWithRuling(PDDocument pd , int pageNumber) {
        Page page = new ObjectExtractor(pd).extract(pageNumber);
        List<List<String>> data  = new ArrayList<>();
        List<Ruling> lines = new ArrayList<>();
        List<Ruling> vertcallines = page.getVerticalRulings();
        List<Ruling> horizcallines = page.getHorizontalRulings();
        for (Ruling r : vertcallines){
            lines.add(r);
        }
        for (Ruling r : horizcallines){
            if (r.getTop()>=vertcallines.get(0).getTop() && r.getBottom()<=vertcallines.get(0).getBottom()){
                lines.add(r);
            }

        }

        List<Table> tables = sea.extract(page,lines);
        for (Table table : tables) {
            List<List<RectangularTextContainer>> rows = table.getRows();
            for (List<RectangularTextContainer> cells : rows) {
                List<String> row = new ArrayList<>();
                for (RectangularTextContainer content : cells) {
                    String text = content.getText(false);
                    row.add(text);
                }
                data.add(row);
            }

        }
        return data;
    }
}
