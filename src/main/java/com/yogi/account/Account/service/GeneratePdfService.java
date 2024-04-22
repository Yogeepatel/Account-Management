package com.yogi.account.Account.service;

import org.apache.pdfbox.pdmodel.PDDocument;

public interface GeneratePdfService {
	
	void generateSummaryPdf();
	
	void drawSummaryPdf(PDDocument document);
	
	void generateFdPdf();
	
	void generateElssPdf();
	
	void bankDetailsPdf();
	
	void generateStocksPdf();
	
	void generateSipPdf();
	
	void generateMfPdf();
	
	void destructmodePdf();
}
