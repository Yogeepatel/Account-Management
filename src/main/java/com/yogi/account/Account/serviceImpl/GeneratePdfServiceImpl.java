package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.entity.ELSS;
import com.yogi.account.Account.entity.FDs;
import com.yogi.account.Account.pdfManipulation.PdfWriter;
import com.yogi.account.Account.repository.ELSSRepository;
import com.yogi.account.Account.repository.FDRepository;
import com.yogi.account.Account.service.GeneratePdfService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.yogi.account.Account.pdfManipulation.PdfWriter.*;

@AllArgsConstructor
@Service
public class GeneratePdfServiceImpl implements GeneratePdfService {
	private PdfWriter pdfWriter;
	private FDRepository fdRepository;
	private ELSSRepository elssRepository;
	
	@Override
	public void generateSummaryPdf() {
		try (PDDocument document = new PDDocument()) {
			drawSummaryPdf(document);
			saveFile(document, "Summary");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	void drawSummaryPdf(PDDocument document) {
		try {
			PDPage page = new PDPage();
			document.addPage(page);
			
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			float margin = 35;
			float ystart = page.getMediaBox().getHeight() - (2 * margin);
			float tableWidth = 350;
			float name_tableHeight = 25;
			nameWriter(contentStream, 4f * margin, ystart, tableWidth, name_tableHeight);
			float summary_tableHeight = 200;
			float summary_tablewidth = tableWidth * 1.5f;
			float summary_rowHeight = summary_tableHeight / 5;
			String[][] textArray = new String[][]{{"Name", "Age"}, {"Email", "Phone"}, {"a", "b"}, {"c", "d"}};
			drawTableBorder(contentStream, 1.75f * margin, ystart - margin * 3.5f, summary_tablewidth, summary_tableHeight);
			drawrow(contentStream, 1.75f * margin, ystart - margin * 3.5f, summary_tablewidth / 2 - 50, 10, tableWidth * 1.5f, summary_rowHeight, summary_tablewidth, summary_rowHeight, 1, true, new String[][]{{"Summary"}}, "#cce6ff", 25);
			drawcolumn(contentStream, 1.75f * margin, ystart - margin * 3.5f - summary_rowHeight, summary_tableHeight - summary_rowHeight, summary_tablewidth / 2, 0, 0, 0, 0, 1, true, null, "#cce6ff", 20);
			drawrow(contentStream, 1.75f * margin, ystart - margin * 3.5f - summary_rowHeight, 5, 10, tableWidth * 1.5f, summary_rowHeight, summary_tablewidth / 7, summary_rowHeight, 4, true, textArray, "#80ccff", 16);
			drawcolumn(contentStream, 1.75f * margin, ystart - margin * 3.5f - summary_rowHeight, summary_tableHeight - summary_rowHeight, summary_tablewidth / 7, 0, 0, 0, 0, 1, false, null, "#80ccff", 20);
			drawcolumn(contentStream, summary_tablewidth / 2, ystart - margin * 3.5f - summary_rowHeight, summary_tableHeight - summary_rowHeight, summary_tablewidth / 3.85f, 0, 0, 0, 0, 1, false, null, "#80ccff", 20);
			contentStream.close();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void generateFdPdf() {
		List<FDs> fdsList = fdRepository.findAll();
		try (PDDocument document = new PDDocument()) {
			drawFDPdf(document);
			saveFile(document, "Fd Details");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	void drawFDPdf(PDDocument document) {
		try {
			List<FDs> fdsList = fdRepository.findAll();
			
			PDPage page = new PDPage();
			
			document.addPage(page);
			PDPageContentStream pdPageContentStream = new PDPageContentStream(document, page);
			Float xStart = 30f;
			Float yStart = 740f;
			Float margin = 50f;
			float rowMargin = 25f;
			String[][] data = new String[fdsList.size()][];
			String[][] headerData = {{"S.No.", "Bank Name", "Scheme Type", "Account Number", "Amount", "Rate of \nInterest", "Account Open\n Date", " Maturity Date"}};
			int i = 0;
			for (FDs f : fdsList) {
				String[] datatemp = {f.getId(), f.getBankName(), f.getType(), f.getNumber(), f.getAmount(), f.getRoi(), f.getAccountOpenDate(), f.getMaturityDate()};
				data[i] = datatemp;
				i = i + 1;
			}
			float datatableY = xStart * 1.5f;
			float tableHeight = rowMargin * fdsList.size() + rowMargin * 1.5f;
			float tableWidth = xStart + 529;
			float[] colwidth = {margin / 1.5f, margin, datatableY + margin * 1f, datatableY + margin * 1.5f, datatableY + margin / 2, margin, datatableY + margin / 2, margin * 1.4f};
			headerText(pdPageContentStream, xStart * 5, yStart, 0, margin / 2, 0, 0, "List of FD", 40, "#0012ff", "#000000");
			nameWriter(pdPageContentStream, xStart * 3, yStart - margin / 2, margin * 7f, margin / 2);
			drawTableBorder(pdPageContentStream, xStart, yStart - 2 * margin, tableWidth, tableHeight);
			contentText(pdPageContentStream, xStart * 7, yStart - 1.80f * margin, 0, margin / 2, 0, 0, "FD Details", 20, "#000000");
			drawcolumns(pdPageContentStream, xStart, yStart - 2f * margin, tableHeight, colwidth, margin, margin, 0, 0, 7, false, null, "#000000", 25);
			drawrow(pdPageContentStream, xStart, yStart - 2 * margin - rowMargin * 1.5f, 5, 5, tableWidth, rowMargin * 1.5f, colwidth, 0, 1, true, headerData, "#80ccff", 8);
			drawrow(pdPageContentStream, xStart, yStart - 2 * margin - rowMargin * 1.5f, 5, 5, tableWidth, 0, colwidth, rowMargin, fdsList.size(), false, data, "#80ccff", 11);
			pdPageContentStream.close();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public void generateElssPdf() {
		
		try (PDDocument document = new PDDocument()) {
			drawElssPdf(document);
			saveFile(document, "Elss Funds");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	void drawElssPdf(PDDocument document) {
		List<ELSS> elssList = elssRepository.findAll();
		PDPage page = new PDPage();
		
		document.addPage(page);
		try {
			PDPageContentStream pdPageContentStream = new PDPageContentStream(document, page);
			
			Float xStart = 30f;
			Float yStart = 740f;
			Float margin = 50f;
			float rowMargin = 25f;
			String[][] data = new String[elssList.size()][];
			String[][] headerData = {{"S.No.", "Folio Number", "Name of Fund", "Units", "Purchase Date", "Amount"}};
			int i = 0;
			for (ELSS elss : elssList) {
				String[] datatemp = {elss.getId(), elss.getFolio_number(), elss.getName_of_fund().split("FUND")[0], elss.getUnits().toString(), elss.getPurchase_Date(), elss.getAmount()};
				data[i] = datatemp;
				i = i + 1;
			}
			float datatableY = xStart * 1.5f;
			float tableHeight = rowMargin * elssList.size() + rowMargin * 1.5f;
			float tableWidth = xStart + 537;
			float[] colwidth = {margin / 1.2f, datatableY + margin * 0.75f, datatableY + margin * 3, datatableY * 1.3f, datatableY + margin, datatableY + margin};
			headerText(pdPageContentStream, xStart * 5, yStart, 0, margin / 2, 0, 0, "List of ELSS Fund", 40, "#0012ff", "#000000");
			nameWriter(pdPageContentStream, xStart * 3, yStart - margin / 2, margin * 7f, margin / 2);
			drawTableBorder(pdPageContentStream, xStart, yStart - 2 * margin, tableWidth, tableHeight);
			contentText(pdPageContentStream, xStart * 7, yStart - 1.80f * margin, 0, margin / 2, 0, 0, "ELSS Fund Details", 20, "#000000");
			drawcolumns(pdPageContentStream, xStart, yStart - 2f * margin, tableHeight, colwidth, 0, 0, 0, 0, headerData[0].length, false, null, "#000000", 25);
			drawrow(pdPageContentStream, xStart, yStart - 2 * margin - rowMargin * 1.5f, 10, 5, tableWidth, rowMargin * 1.5f, colwidth, 0, 1, true, headerData, "#80ccff", 10);
			drawrow(pdPageContentStream, xStart, yStart - 2 * margin - rowMargin * 1.5f, 10, 5, tableWidth, 0, colwidth, rowMargin, elssList.size(), false, data, "#80ccff", 11);
			pdPageContentStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void saveFile(PDDocument document, String filename) {
		try {
			document.save(StringUtils.join("src/main/resources/generatedpdfs/", filename, ".pdf"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
