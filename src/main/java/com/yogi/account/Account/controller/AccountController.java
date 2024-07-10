package com.yogi.account.Account.controller;

import com.yogi.account.Account.dto.BulkUpdateFileUploadMetaDataRequest;
import com.yogi.account.Account.dto.LoginDto;
import com.yogi.account.Account.dto.ResponseString;
import com.yogi.account.Account.dto.UpdateRequestDto;
import com.yogi.account.Account.entity.UploadMetaData;
import com.yogi.account.Account.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
@CrossOrigin
@Slf4j
@RestController
public class AccountController {
	private GeneratePdfService generatePdfService;
	private FetchMfStocksService fetchMfStocksService;
	private FetchBankDataService fetchBankDataService;
	private PasswordService passwordService;
	private FileUploadMetadataService fileUploadMetadataService;
	
	@PostMapping("/login")
	public String login(@RequestBody LoginDto loginDto) {
		return passwordService.login(loginDto.getUsername(), loginDto.getPassword());
	}
	
	@GetMapping("/key/{key}")
	public String validateKey(@PathVariable int key) {
		return passwordService.validateKey(key);
	}
	
	@GetMapping("/passwordString")
	public String passwordString() {
		return (String) passwordService.getpasswordhelper();
	}
	
	/*for saving fileuploadmetadata in db*/
	@PostMapping("/uploadFileMetaDataInfo")
	public String saveuploadMetaData(@RequestBody UploadMetaData data){
		return fileUploadMetadataService.saveuploadMetaData(data);
	}
	
	@PostMapping("/uploadMonthDependentMetaData/{type}")
	public UploadMetaData monthDependentUploadMetaData(@PathVariable String type, @RequestParam int expenditureButton, @RequestBody List<String> attachmentTitleKey){
		return  fileUploadMetadataService.monthDependentUploadMetaData(type,expenditureButton,attachmentTitleKey);
	}
	
	@GetMapping("/fileUploadMetaData/{type}")
	public UploadMetaData fileUploadMetaData(@PathVariable String type){
		return fileUploadMetadataService.fileUploadMetaData(type);
	}
	
	
	@PostMapping(value = "/fileUploadMetaData/bulkupdate")
	public UploadMetaData bulkUpdateFileUploadMetaData(@RequestBody BulkUpdateFileUploadMetaDataRequest categoryList , @RequestParam int buttonNumber){
		if(buttonNumber==1)
			return fileUploadMetadataService.bulkUpdateBankAccountFileUploadMetaData(categoryList);
		else if(buttonNumber==2)
			return fileUploadMetadataService.bulkUpdateInvestmentAccountFileUploadMetaData(categoryList);
		else
		throw new RuntimeException("Invalid input request.");
	}
	
	/* summary - Uploads a file*/
	@PostMapping("/uploadfile")
	public String uploadFile(@RequestParam String type, @RequestParam("file") MultipartFile file) {
		if(type.equalsIgnoreCase("monthly") || type.equalsIgnoreCase("salaryUsage")){
			type = "expenditure/"+type;
		}
		
		if (ObjectUtils.isEmpty(file) )
			throw new RuntimeException("Invalid File");
		try (FileOutputStream fileOutputStream = new FileOutputStream( "src/main/resources/UploadedFiles/"+type +"/"+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy  ")) +file.getOriginalFilename())
		){
			System.out.println("abc");
						fileOutputStream.write(file.getBytes());
			
			
			return "File saved successfully.";		}
		catch (Exception e) {
			throw  new RuntimeException("Error while saving file: " + e.getMessage());
			
		}
	}
	
	@GetMapping("/deletefile")
	public String deleteFile(@RequestParam String type,@RequestParam String filename) {
		if(type.equalsIgnoreCase("monthly")|| type.equalsIgnoreCase("salaryUsage") ){
			type = "expenditure/"+type;
		}
		if (ObjectUtils.isEmpty(filename) )
			throw new RuntimeException("No such file present");
		try {
			
			File newfile =ResourceUtils.getFile("src/main/resources/UploadedFiles/" + type +"/"+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy  ")) +filename);
			boolean isDeleted = newfile.delete();
			if (isDeleted)
			return "File deleted successfully.";
			else
				throw  new RuntimeException("Error while deleting the file: ");
		} catch (Exception e) {
			throw  new RuntimeException("Error while deleting the file: " + e.getMessage());

		}
	}
	
	/* Downloads a File*/
	@GetMapping("/downloadpdf")
	public ResponseEntity<byte[]> downloadPdf(@RequestParam String type,@RequestParam String component) throws IOException {
		String filename = "";
		switch (component) {
			case "stocks"->{
				filename = "Stocks";
				generatePdfService.generateStocksPdf();}
			case "elss"->{
				filename = "Elss Funds";
				generatePdfService.generateElssPdf();}
			case "fd" ->{
				filename = "Fd Details";
				generatePdfService.generateFdPdf();}
			case "mutualfund" ->{
				filename = "Mutual Funds";
				generatePdfService.generateMfPdf();}
			case "sip"->{
				filename = "SIP";
				generatePdfService.generateSipPdf();}
			case "accountdetails" -> {
				filename = "Bank Details";
				generatePdfService.bankDetailsPdf();}
		}
		byte[] bytes = Files.readAllBytes(ResourceUtils.getFile("classpath:generatedpdfs/" + filename+".pdf").toPath());
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".pdf");
		
		return ResponseEntity.ok()
				.headers(headers)
				.body(bytes);
	}
	
	
	/* Endpoint for saving bank details and monthly expenditure and FDs*/
	@Transactional
	@PostMapping("/bulkupdate/bankdata")
	public ResponseString addStatementDetails(@RequestParam String month, @RequestBody UpdateRequestDto categoryToUpdate)   {
		List<String> categories = new ArrayList<>();
				 categoryToUpdate.getBankAccountCategoriesToUpdate().forEach(value -> categories.add(value));
		UploadMetaData attachmentTitle = fileUploadMetadataService.bulkUpdateBankAccountFileUploadMetaData(new BulkUpdateFileUploadMetaDataRequest(categoryToUpdate.isUpdateAllBankAccounts(), categoryToUpdate.isUpdateAllInvestmentAccounts(), categories,categoryToUpdate.getInvestmentAccountcategoriesToUpdate()));
		Map<String,String> fileNameMap = categoryToUpdate.getFilesNameMap();
		Map<String, String> attachmentTitleMap = attachmentTitle.getAttachmentTitle();
		
		Map<String, String> iciciPaths = new HashMap<>();
		for (Map.Entry<String, String> key : attachmentTitleMap.entrySet()) {
			String fileName = fileNameMap.getOrDefault(key.getValue(),"");
			if(StringUtils.isBlank(fileName)){
				throw new RuntimeException("All required file not uploaded.");
			}
			
			String path = "bulkupdate/" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy  ")) + fileName;
			if(key.getValue().toLowerCase().contains("sbi"))
				fetchBankDataService.fetchSbiStatementData(path);
			else if(key.getValue().toLowerCase().contains("ubi"))
				fetchBankDataService.fetchUbiStatementData(path);
			else if(key.getValue().equalsIgnoreCase("Upload Non-Tabulated ICICI Pdf Statement")) {
				if (!StringUtils.isBlank(fileName))
					if (!StringUtils.isBlank(fileName))
						iciciPaths.put("Non-Tabulated",path);
					else iciciPaths.put("Non-Tabulated","");
			}else if(key.getValue().equalsIgnoreCase("Upload Tabulated ICICI Pdf Statement")) {
				if (!StringUtils.isBlank(fileName))
					iciciPaths.put("Tabulated",path);
				else iciciPaths.put("Tabulated","");
			}
			
			
			fileNameMap.replace(key.getValue(), path);
		}
		if(!iciciPaths.isEmpty())
			fetchBankDataService.fetchIciciStatementData(iciciPaths.get("Tabulated"), iciciPaths.get("Non-Tabulated"));
		
		
		if (categoryToUpdate.isUpdateAllBankAccounts()) {
			try {
				return new ResponseString(StringUtils.join(
						fetchBankDataService.salaryUsage(month), "\n",
						fetchBankDataService.collectFdDataofUBI(), '\n',
						fetchBankDataService.collectFdDataofSBI(), '\n',
						fetchBankDataService.expenditureDataofSBI(month), '\n',
						fetchBankDataService.expenditureDataofUBI(month), '\n',
						fetchBankDataService.saveBankDetailsofSBI(), '\n',
						fetchBankDataService.saveBankDetailsofUBI(), "\n",
						fetchBankDataService.saveBankDetailsofIcici() + "\n",
						fetchBankDataService.expenditureDataofIcici(month) + "\n",
						fetchBankDataService.yearlyExpenditure()).trim());
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseString("Failed to Update Fd,Expenditure,Bankdetails Data. Error: " + e.getMessage());
			}
		}
		else {
			if (!categoryToUpdate.getBankAccountCategoriesToUpdate().isEmpty()) {
				String[] bankName = {"State Bank of India", "Union Bank of India", "ICICI"};
				StringBuilder result = new StringBuilder();
				for (String key : categoryToUpdate.getBankAccountCategoriesToUpdate()) {
					String[] uploadData = key.split("\n");
					if (uploadData[0].equalsIgnoreCase("salaryUsage")){
						result.append(fetchBankDataService.salaryUsage(month)).append('\n');
					}
					if (uploadData[0].equalsIgnoreCase("Expenditure")) {
						if (uploadData[1].equalsIgnoreCase(bankName[0]))
							result.append(fetchBankDataService.expenditureDataofSBI(month)).append('\n');
						
						if (uploadData[1].equalsIgnoreCase(bankName[1]))
							result.append(fetchBankDataService.expenditureDataofUBI(month)).append('\n');
						if (uploadData[1].equalsIgnoreCase(bankName[2])){
							result.append(fetchBankDataService.expenditureDataofIcici(month)).append('\n');}
						result.append(fetchBankDataService.yearlyExpenditure()).append('\n');
					}
					if (uploadData[0].equalsIgnoreCase("Account Details")) {
						if (uploadData[1].equalsIgnoreCase(bankName[0]))
							result.append(fetchBankDataService.saveBankDetailsofSBI()).append('\n');
						if (uploadData[1].equalsIgnoreCase(bankName[1]))
							result.append(fetchBankDataService.saveBankDetailsofUBI()).append('\n');
						if (uploadData[1].equalsIgnoreCase(bankName[2]))
							result.append(fetchBankDataService.saveBankDetailsofIcici()).append('\n');
					}
					if (uploadData[0].equalsIgnoreCase("Fixed Deposits(FD)")) {
						if (uploadData[1].equalsIgnoreCase(bankName[0]))
							result.append(fetchBankDataService.collectFdDataofSBI()).append('\n');
						if (uploadData[1].equalsIgnoreCase(bankName[1]))
							result.append(fetchBankDataService.collectFdDataofUBI()).append('\n');
						
						
					}
					
					
				}
				return new ResponseString(result.toString().trim());
			}
			else
				return new ResponseString("No Categories Selected to Update.");
		}
		
		
	}
	@Transactional
	@PostMapping("/bulkupdate/mfstocksdata")
	public ResponseString addMfandStocksDetails( @RequestBody UpdateRequestDto categoryToUpdate)   {
		String elssPath = "";
		UploadMetaData attachmentTitle = fileUploadMetadataService.bulkUpdateInvestmentAccountFileUploadMetaData(new BulkUpdateFileUploadMetaDataRequest(categoryToUpdate.isUpdateAllBankAccounts(), categoryToUpdate.isUpdateAllInvestmentAccounts(), categoryToUpdate.getBankAccountCategoriesToUpdate(),categoryToUpdate.getInvestmentAccountcategoriesToUpdate()));
		for (Map.Entry<String, String> key : attachmentTitle.getAttachmentTitle().entrySet()) {
			String fileName = categoryToUpdate.getFilesNameMap().getOrDefault(key.getValue(),"");
			if(StringUtils.isBlank(fileName)){
				throw new RuntimeException("All required file not uploaded.");
			}
			
			String path = "bulkupdate/" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy  ")) + fileName;
			if(key.getValue().equalsIgnoreCase("Upload All Holding Pdf File")){
				fetchMfStocksService.fetchStocksAndMfMetaData(path);
				fetchMfStocksService.fetchStocksAndMfTransactionStatement(path);}
			if(key.getValue().equalsIgnoreCase("Upload All Holdings Csv File")){
		        fetchMfStocksService.readMfCsvFile(path);
			fetchMfStocksService.readstocksCsvFile(path);}
			if(key.getValue().equalsIgnoreCase("Upload ELSS Holding Pdf File"))
				elssPath = path;
			
		}
		if (categoryToUpdate.isUpdateAllInvestmentAccounts()) {
			try {
				return new ResponseString(StringUtils.join(
						fetchMfStocksService.addElssStatementData(elssPath), '\n',
						fetchMfStocksService.saveStocks(), '\n',
						fetchMfStocksService.saveSipandMf(), '\n',
						fetchMfStocksService.saveAccountDetails()
				));
			} catch (Exception e) {
				return new ResponseString("Failed to Update Stocks,MF,Sip Data. Error: " + e.getMessage());
			}
		}
		else{
			StringBuilder result= new StringBuilder();
			for (String category:categoryToUpdate.getInvestmentAccountcategoriesToUpdate()){
				if(category.equalsIgnoreCase("Stocks"))
					result.append(fetchMfStocksService.saveStocks()).append("\n");
				if(category.equalsIgnoreCase("Mutual Funds")||category.equalsIgnoreCase("Systmatic Investment Plan(SIP)"))
					result.append(fetchMfStocksService.saveSipandMf()).append("\n");
				if(category.equalsIgnoreCase("ELSS"))
					result.append(fetchMfStocksService.addElssStatementData(elssPath)).append("\n");
				
			}
			return new ResponseString(result.toString());
		}
	}
	
	
	
}
