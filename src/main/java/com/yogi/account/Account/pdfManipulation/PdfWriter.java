package com.yogi.account.Account.pdfManipulation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.*;

@Component
public class PdfWriter {
	
	public static void nameWriter(PDPageContentStream contentStream, float xStart, float yStart, float tableWidth,
	                              float tableHeight) throws IOException {
		contentStream.setLineWidth(1);
		contentStream.setNonStrokingColor(Color.BLACK);
		drawTableBorder(contentStream, xStart, yStart, tableWidth, tableHeight);
		contentStream.moveTo(xStart + 70, yStart);
		contentStream.lineTo(xStart + 70, yStart - tableHeight);
		contentStream.beginText();
		contentStream.setFont(new PDType1Font(HELVETICA_BOLD), 16);
		contentStream.newLineAtOffset(xStart + 10, yStart - 17);
		contentStream.showText("Name");
		contentStream.endText();
		contentStream.stroke();
		contentStream.beginText();
		contentStream.setFont(new PDType1Font(HELVETICA), 16);
		contentStream.newLineAtOffset(xStart + 90, yStart - 17);
		contentStream.showText("Yogendra Kumar Patel");
		contentStream.endText();
		contentStream.stroke();
	}
	
	public static void drawTableBorder(PDPageContentStream contentStream, float xStart, float yStart, float tableWidth,
	                                   float tableHeight) throws IOException {
		contentStream.setLineWidth(1);
		
		contentStream.moveTo(xStart, yStart);
		contentStream.lineTo(xStart + tableWidth, yStart);
		contentStream.lineTo(xStart + tableWidth, yStart - tableHeight);
		contentStream.lineTo(xStart, yStart - tableHeight);
		contentStream.lineTo(xStart, yStart);
		contentStream.stroke();
	}
	
	public static void drawrows(PDPageContentStream contentStream, float xStart, float yStart, float offsetx, float offsety, float tableWidth,
	                            float height, float width, float[] rowHeight, int numberOfRows, boolean isHeader, String[][] text, String color, int fontsize) throws IOException {
		float temp_y = yStart;
		float temp_x = xStart;
		for (int i = 0; i < numberOfRows; i++) {
			int value = i + 1;
			temp_y = temp_y - rowHeight[i];
			contentStream.moveTo(xStart, temp_y);
			contentStream.lineTo(xStart + tableWidth, temp_y);
			contentStream.stroke();
			if (isHeader && (text[i] != null || text[i].length != 0)) {
				for (int j = 0; j < text[i].length; j++) {
					headerText(contentStream, temp_x, temp_y, width, offsetx, offsety, height, text[i][j], fontsize, "#000000", color);
				}
				temp_x = temp_x + (tableWidth / 2);
			} else if (!isHeader && (text[i] != null || text[i].length != 0)) {
				for (int j = 0; j < text[i].length; j++) {
					contentText(contentStream, temp_x, temp_y, width, offsetx, offsety, height, text[i][j], fontsize);
					temp_x = temp_x + (tableWidth / 2);
				}
				
			}
		}
	}
	
	public static void drawrow(PDPageContentStream contentStream, float xStart, float yStart, float offsetx, float offsety, float tableWidth,
	                           float height, float width, float rowHeight, int numberOfRows, boolean isHeader, String[][] text, String color, int fontsize) throws IOException {
		float temp_y = yStart;
		float temp_x = xStart;
		for (int i = 0; i < numberOfRows; i++) {
			temp_y = temp_y - rowHeight;
			temp_x = xStart;
			contentStream.moveTo(xStart, temp_y);
			
			contentStream.lineTo(xStart + tableWidth, temp_y);
			contentStream.stroke();
			if (isHeader && !ObjectUtils.isEmpty(text) && !ObjectUtils.isEmpty(text[i])) {
				for (int j = 0; j < text[i].length; j++) {
					headerText(contentStream, temp_x, temp_y, width, offsetx, offsety, height, text[i][j], fontsize, "#000000", color);
					temp_x = temp_x + width;
					System.out.println(text[i][j] + " " + temp_x);
				}
			} else if (!isHeader && !ObjectUtils.isEmpty(text) && !ObjectUtils.isEmpty(text[i])) {
				for (int j = 0; j < text[i].length; j++) {
					contentText(contentStream, temp_x, temp_y, width, offsetx, offsety, height, text[i][j], fontsize);
					temp_x = temp_x + width;
				}
				
			}
		}
	}
	
	public static void drawrow(PDPageContentStream contentStream, float xStart, float yStart, float offsetx, float offsety, float tableWidth,
	                           float height, float width[], float rowHeight, int numberOfRows, boolean isHeader, String[][] text, String color, int fontsize) throws IOException {
		float temp_y = yStart;
		float temp_x = xStart;
		for (int i = 0; i < numberOfRows; i++) {
			temp_y = temp_y - rowHeight;
			temp_x = xStart;
			contentStream.moveTo(xStart, temp_y);
			contentStream.lineTo(xStart + tableWidth, temp_y);
			contentStream.stroke();
			if (isHeader && !ObjectUtils.isEmpty(text) && !ObjectUtils.isEmpty(text[i])) {
				for (int j = 0; j < text[i].length; j++) {
					headerText(contentStream, temp_x, temp_y, width[j], offsetx, offsety, height, text[i][j], fontsize, "#000000", color);
					temp_x = temp_x + width[j];
					System.out.println(text[i][j] + " " + temp_x);
				}
			} else if (!isHeader && !ObjectUtils.isEmpty(text) && !ObjectUtils.isEmpty(text[i])) {
				for (int j = 0; j < text[i].length; j++) {
					contentText(contentStream, temp_x, temp_y, width[j], offsetx, offsety, height, text[i][j], fontsize);
					temp_x = temp_x + width[j];
				}
				
			}
		}
	}
	
	public static void drawcolumn(PDPageContentStream contentStream, float xStart, float yStart, float tableHeight, float columnWidths, float height, float width, float offsetx, float offsety, int numberOfColumns, boolean isHeader, String[] text, String color, int fontsize) throws IOException {
		float temp_x = xStart;
		float temp_y = yStart - tableHeight;
		for (int i = 0; i < numberOfColumns; i++) {
			temp_x = temp_x + columnWidths;
			contentStream.moveTo(temp_x, yStart);
			contentStream.lineTo(temp_x, temp_y);
			contentStream.stroke();
			if (isHeader && text != null) {
				headerText(contentStream, temp_x - columnWidths, temp_y, columnWidths, offsetx, offsety, height, text[i], fontsize, "#000000", color);
			}
			if (!isHeader && text != null) {
				contentText(contentStream, temp_x - columnWidths, temp_y, columnWidths, offsetx, offsety, height, text[i], fontsize);
				
			}
		}
	}
	
	public static void drawcolumns(PDPageContentStream contentStream, float xStart, float yStart, float tableHeight, float columnWidths[], int numberOfColumns) throws IOException {
		float temp_x = xStart;
		for (int i = 0; i < numberOfColumns; i++) {
			int value = i + 1;
			temp_x = temp_x + columnWidths[i];
			contentStream.moveTo(temp_x, yStart);
			contentStream.lineTo(temp_x, yStart - tableHeight);
			contentStream.stroke();
//			if (isHeader && text != null) {
//				headerText(contentStream, temp_x, yStart, width, offsetx, offsety, height, text[i], fontsize, "#000000",color);
//			}
//			if (!isHeader && text != null) {
//				contentText(contentStream, temp_x, yStart, width, offsetx, offsety, height, text[i], fontsize, color);
//			}
		}
	}
	
	public static void writeMappedText(PDPageContentStream contentStream, float xStart, float yStart, float rowHeight, int noOfRows, float[] columnWidth, float offsetx, float offsety, Map<String, String> text, String[] keyList, int fontsize, String color, String bgColor) {
		System.out.println(keyList);
		try {
			int mapcount = 0;
			float temp_y = yStart;
			float temp_x = xStart;
			for (int i = 0; i < noOfRows - 1; i++) {
				temp_y = temp_y - rowHeight;
				temp_x = xStart;
				System.out.println(columnWidth.length);
				float width;
				for (int j = 0; j < columnWidth.length + 1; j++) {
					if (j == columnWidth.length) {
						width = columnWidth[j - 2];
					} else {
						width = columnWidth[j];
					}
					String key = keyList[mapcount];
					
					if (j % 2 == 0) {
						System.out.println("printed");
						headerText(contentStream, temp_x, temp_y, width, offsetx, offsety, rowHeight, key, fontsize, color, bgColor);
					} else {
						mapcount = mapcount + 1;
						System.out.println(text.get(key) + key);
						contentText(contentStream, temp_x, temp_y, width, offsetx, offsety, rowHeight, text.get(key), fontsize);
						System.out.println("printed");
					}
					temp_x = temp_x + width;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void headerText(PDPageContentStream contentStream, float xStart, float yStart, float width, float offsetx, float offsety, float height, String text, int fontSize, String textcolor, String bgcolor) throws IOException {
		
		contentStream.setNonStrokingColor(Color.decode(bgcolor));
		contentStream.addRect(xStart + 1, yStart + 1, width - 2f, height - 2f);
		contentStream.fill();
		contentStream.beginText();
		contentStream.setFont(new PDType1Font(HELVETICA_BOLD), fontSize);
		contentStream.setNonStrokingColor(Color.decode(textcolor));
		
		
		if (text.contains("\n")) {
			contentStream.newLineAtOffset(xStart + offsetx, yStart + offsety + 15);
			String[] texts = text.split("\n");
			for (String s : texts) {
				contentStream.showText(s);
				contentStream.endText();
				contentStream.beginText();
				contentStream.newLineAtOffset(xStart + offsetx, yStart + offsety);
				
				
			}
		} else {
			contentStream.newLineAtOffset(xStart + offsetx, yStart + offsety);
			contentStream.showText(text);
		}
		contentStream.endText();
		
		
	}
	
	public static void contentText(PDPageContentStream contentStream, float xStart, float yStart, float width, float offsetx, float offsety, float height, String text, int fontSize) throws IOException {
		
		contentStream.beginText();
		contentStream.setFont(new PDType1Font(TIMES_ROMAN), fontSize);
		contentStream.setNonStrokingColor(Color.black);
		contentStream.newLineAtOffset(xStart + offsetx, yStart + offsety);
		contentStream.showText(text);
		contentStream.endText();
		
		
	}
	
	
	public void drawHorizontalLine(PDDocument document, float x1, float x2, float y, String color) {
		try {
			PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0), PDPageContentStream.AppendMode.APPEND, true);
			contentStream.setStrokingColor(Color.decode("#" + color));
			contentStream.moveTo(x1, y);
			contentStream.lineTo(x2, y);
			contentStream.stroke();
			contentStream.close();
			document.save("modified.pdf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void drawVerticalLine(PDDocument document, float y1, float y2, float x, String color) {
		try {
			PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0), PDPageContentStream.AppendMode.APPEND, true);
			contentStream.setLineWidth(2);
			contentStream.setStrokingColor(Color.decode("#" + color));
			contentStream.moveTo(x, y1);
			contentStream.lineTo(x, y2);
			contentStream.stroke();
			contentStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
