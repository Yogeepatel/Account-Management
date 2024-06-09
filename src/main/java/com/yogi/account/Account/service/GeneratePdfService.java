package com.yogi.account.Account.service;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.util.Map;

public interface GeneratePdfService {
	
	void generateSummaryPdf();
	
	void drawSummaryPdf(PDDocument document);
	
	Map<String , String> calculateSummaryDetails();
	
	void generateFdPdf();
	
	void generateElssPdf();
	
	void bankDetailsPdf();
	
	void generateStocksPdf();
	
	void generateSipPdf();
	
	void generateMfPdf();
	
	void destructmodePdf();
}
