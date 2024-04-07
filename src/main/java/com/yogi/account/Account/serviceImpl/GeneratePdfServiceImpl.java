package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.entity.AccountExpenditure;
import com.yogi.account.Account.entity.BankDetails;
import com.yogi.account.Account.entity.ELSS;
import com.yogi.account.Account.entity.FDs;
import com.yogi.account.Account.pdfManipulation.PdfWriter;
import com.yogi.account.Account.repository.AccountExpenditureRepository;
import com.yogi.account.Account.repository.BankDetailsRepositiory;
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
import java.util.*;

import static com.yogi.account.Account.pdfManipulation.PdfWriter.*;

@AllArgsConstructor
@Service
public class GeneratePdfServiceImpl implements GeneratePdfService {
	private PdfWriter pdfWriter;
	private FDRepository fdRepository;
	private ELSSRepository elssRepository;
	private BankDetailsRepositiory bankDetailsRepositiory;
	private AccountExpenditureRepository accountExpenditureRepository;
	
	@Override
	public void generateSummaryPdf() {
		try (PDDocument document = new PDDocument()) {
			drawSummaryPdf(document);
			saveFile(document, "Summary");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void drawSummaryPdf(PDDocument document) {
		List<AccountExpenditure> expenditureList = accountExpenditureRepository.findAll();
		Optional<AccountExpenditure> sbiBalance = expenditureList.stream().filter((id)->id.getId().equals("SBI")).findFirst();
		Optional<AccountExpenditure> ubiBalance = expenditureList.stream().filter((id)->id.getId().equals("UBI")).findFirst();
		Optional<AccountExpenditure> iciciBalance = expenditureList.stream().filter((id)->id.getId().equals("ICICI")).findFirst();
		String[][] balances = {{ubiBalance.isPresent()?String.valueOf(sbiBalance.get().getClosingBalance()).trim():"", ubiBalance.isPresent()? String.valueOf(ubiBalance.get().getClosingBalance()).trim():""}};
		System.out.println(sbiBalance.get());
		try {
			PDPage page = new PDPage();
			document.addPage(page);
			
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			
			float margin = 50;
			float contentXStart = 2 * margin;
			float ystart = page.getMediaBox().getHeight() - (2 * margin);
			float tableWidth = 350;
			float name_tableHeight = 25;
			headerText(contentStream,contentXStart+margin*1.3f, ystart, 0, 0,0,0,"ALL FINANCIAL DETAILS",25,"#80ccff","#ffffff");
			nameWriter(contentStream, contentXStart, ystart - margin, tableWidth, name_tableHeight);
			float summary_tableHeight = 240;
			float summary_tablewidth = tableWidth * 1.5f;
			float summary_rowHeight = 42;
			float tableheight_AccountRow = ystart - margin * 3.5f - 4* summary_rowHeight;
			String[] headerText = new String[]{"FD","ELSS"};
			Map<String , String> data = calculateSummaryDetails();
			drawTableBorder(contentStream, margin, ystart - margin * 3.5f, summary_tablewidth, summary_tableHeight);
			drawrow(contentStream, margin, ystart - margin * 3.5f, summary_tablewidth / 2 - 50, 10, tableWidth * 1.5f, summary_rowHeight, summary_tablewidth, summary_rowHeight, 1, true, new String[][]{{"Summary"}}, "#cce6ff", 25);
			drawcolumns(contentStream, margin, ystart - margin * 3.5f - summary_rowHeight, summary_tableHeight - 2.7f*summary_rowHeight, new float[]{summary_tablewidth/6,summary_tablewidth/3,summary_tablewidth/6,summary_tablewidth/3}, 4);
			drawrow(contentStream, margin, ystart - margin * 3.5f - summary_rowHeight, 5, 10, tableWidth * 1.5f, summary_rowHeight, summary_tablewidth / 7, summary_rowHeight, 3, true, null, "#80ccff", 16);
			drawrow(contentStream, margin, tableheight_AccountRow , summary_tablewidth / 2 - 50, 5, tableWidth * 1.5f, summary_rowHeight -margin/3, summary_tablewidth, summary_rowHeight-margin/3, 1, true, new String[][]{{"Bank Acount Balance"}}, "#cce6ff", 13);
			drawrow(contentStream, margin, tableheight_AccountRow -margin/2.5f, margin+10, 5, tableWidth * 1.5f, summary_rowHeight -margin/3, summary_tablewidth/3, summary_rowHeight-margin/3, 1, false, new String[][]{{"SBI","UBI","ICICI"}}, "#cce6ff", 13);
			writeMappedText(contentStream,margin,ystart - margin * 3.5f - summary_rowHeight,summary_rowHeight,2,new float[]{summary_tablewidth/6,summary_tablewidth/3,summary_tablewidth/6},10,15,data,headerText,20,"#000000","#80ccff");
			drawcolumn(contentStream,margin,tableheight_AccountRow -margin/2f,margin-2,tableWidth/2,0,0,0,0,2,false,null,"#000000",0);
			drawrow(contentStream,margin,tableheight_AccountRow -margin/2f,margin+10,10,summary_tablewidth,margin-2,summary_tablewidth/3,margin-2,1,false,balances,"000000",13);
			contentStream.close();
		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Map<String , String> calculateSummaryDetails(){
		List<FDs> fdsList = fdRepository.findAll();
		Map<String , String>  summaryData = new HashMap<>();
		Double fdAmount = 0.0;
		for (FDs fDs : fdsList){
			fdAmount += Double.parseDouble(fDs.getAmount().replace(",",""));
		}
		summaryData.put("FD",StringUtils.join("Rs. ",fdAmount));
		List<ELSS> elssList = elssRepository.findAll();
		Double elssamount=0.0;
		for (ELSS elss : elssList){
			String data = elss.getAmount();
			String[] d = data.split(" ");
			elssamount = elssamount+ Double.parseDouble(data.split(" ")[1]);
		}
		summaryData.put("ELSS",StringUtils.join("Rs. ",elssamount));
		return summaryData;
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
			contentText(pdPageContentStream, xStart * 7, yStart - 1.80f * margin, 0, margin / 2, 0, 0, "FD Details", 20);
			drawcolumns(pdPageContentStream, xStart, yStart - 2f * margin, tableHeight, colwidth, headerData[0].length);
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
				String[] datatemp = {StringUtils.join(elss.getId()) ,elss.getFolio_number(), elss.getName_of_fund().trim().split("-")[0], String.valueOf(elss.getUnits()), elss.getPurchase_Date(), elss.getAmount()};
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
			contentText(pdPageContentStream, xStart * 7, yStart - 1.80f * margin, 0, margin / 2, 0, 0, "ELSS Fund Details", 20);
			drawcolumns(pdPageContentStream, xStart, yStart - 2f * margin, tableHeight, colwidth, headerData[0].length);
			drawrow(pdPageContentStream, xStart, yStart - 2 * margin - rowMargin * 1.5f, 10, 5, tableWidth, rowMargin * 1.5f, colwidth, 0, 1, true, headerData, "#80ccff", 10);
			drawrow(pdPageContentStream, xStart, yStart - 2 * margin - rowMargin * 1.5f, 10, 5, tableWidth, 0, colwidth, rowMargin, elssList.size(), false, data, "#80ccff", 11);
			pdPageContentStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void drawBankDetailsPdf(PDDocument document){
		try{
			List<BankDetails> bankDetailsList = bankDetailsRepositiory.findAll();
		PDPage page = new PDPage();
		
		document.addPage(page);
		PDPageContentStream pdPageContentStream = new PDPageContentStream(document, page);
		
		Float xStart = 30f;
		Float yStart = 740f;
		Float margin = 50f;
		float rowMargin = 25f;
		String[][] data = new String[bankDetailsList.size()][];
		String[] headerData = {"Name", "Account Number","Ifsc Code", "Branch", "MICR","Customer ID","Address"};
		int i = 0;
		headerText(pdPageContentStream, xStart * 3, yStart, 0, margin / 2, 0, 0, "Bank Account Details", 40, "#0012ff", "#000000");
		nameWriter(pdPageContentStream, xStart * 5, yStart - margin / 2, margin * 7f, margin / 2);
		
		for (BankDetails bankDetails : bankDetailsList) {
			Map<String ,String> text = new HashMap<>();
			text.put(headerData[0],bankDetails.getName());
			text.put(headerData[1],bankDetails.getAccountNumber().trim());
			text.put(headerData[2],bankDetails.getIfscCode());
			text.put(headerData[3],bankDetails.getBranch());
			System.out.println(bankDetails.getBranch());
			text.put(headerData[4],bankDetails.getMicrCode());
			text.put(headerData[5],bankDetails.getCustomerId());
			
			String address ="";
			for(String temp:bankDetails.getAddress().split(" ") ){
				if(!address.contains(temp)){
					address = StringUtils.join(address," ",temp);
				}
			}
			String[][] row1Data = {{bankDetails.getId()}};
			System.out.println(text);
			i = i + 1;
			float tableHeight = rowMargin * (headerData.length/2+2);
			float tableWidth = margin * 1.5f + margin * 3.5f + margin * 2f + margin * 3.5f;
			float[] colwidth = {margin * 1.5f, margin * 3.5f, margin * 2.3f};
			drawTableBorder(pdPageContentStream, xStart * 2-10, yStart - 2 * margin, tableWidth, tableHeight);
			contentText(pdPageContentStream, xStart * 7, yStart - 1.80f * margin, 0, margin / 2, 0, 0, StringUtils.join(bankDetails.getId().substring(0,1),bankDetails.getId().substring(1).toLowerCase(), "Account Details"), 20);
			drawrow(pdPageContentStream, xStart * 2 -10, yStart - 2 * margin , tableWidth/2f, 5, tableWidth, rowMargin, tableWidth, rowMargin, 1, true, row1Data, "#80ccff", 18);
			drawcolumns(pdPageContentStream,xStart*2-10,yStart - 2 * margin-rowMargin,tableHeight-2*rowMargin,colwidth,colwidth.length);
			drawrow(pdPageContentStream, xStart * 2-10, yStart - 2 * margin , tableWidth/2f, 5, tableWidth, rowMargin, tableWidth, rowMargin, 4, true, null, "#80ccff", 15);
			writeMappedText(pdPageContentStream,xStart*2 - 10,yStart - 2 * margin-rowMargin,rowMargin,headerData.length/2+1,colwidth, 5,5,text,headerData,12,"#000000","#80ccff");
			drawcolumn(pdPageContentStream,xStart*2-10,yStart - 2 * margin-4*rowMargin,tableHeight-rowMargin-3*rowMargin, colwidth[0],rowMargin,colwidth[0],5,5,1,true, Arrays.copyOfRange(headerData,headerData.length-1,headerData.length),"#80ccff",12);
			contentText(pdPageContentStream,xStart*2-10+colwidth[0],yStart - 2 * margin-5*rowMargin,margin*5.8f,5,5,rowMargin,address,12 );
			
			yStart = yStart -  tableHeight - 2*margin;
			
		}
			pdPageContentStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void bankDetailsPdf() {
		
		
		try (PDDocument document = new PDDocument()){
			drawBankDetailsPdf(document);
			saveFile(document,"Bank Details");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void destructmodePdf() {
		
		
		try (PDDocument document = new PDDocument()){
			drawSummaryPdf(document);
			drawBankDetailsPdf(document);
			drawFDPdf(document);
			drawElssPdf(document);
			saveFile(document,"Destruct Mode All Details Pdf");
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
