package com.yogi.account.Account.controller;

import com.yogi.account.Account.entity.LoginDto;
import com.yogi.account.Account.service.FetchBankDataService;
import com.yogi.account.Account.service.FetchMfStocksService;
import com.yogi.account.Account.service.GeneratePdfService;
import com.yogi.account.Account.service.PasswordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;


@AllArgsConstructor
@CrossOrigin
@Slf4j
@RestController
public class AccountController {
	private GeneratePdfService generatePdfService;
	private FetchMfStocksService fetchMfStocksService;
	private FetchBankDataService fetchBankDataService;
	private PasswordService passwordService;
	
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
	
	/* summary - Uploads a file*/
	@GetMapping("/uploadpdf")
	public String uploadpdfs(@RequestParam String filename, @RequestBody byte[] filecontent) {
		if (StringUtils.isEmpty(filename))
			throw new RuntimeException("Invalid File Uploaded");
		try (FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/UplodedFiles" + filename)) {
			fileOutputStream.write(filecontent);
			
			return "PDF file saved successfully.";
		} catch (Exception e) {
			return ("Error saving PDF file: " + e.getMessage());
			
		}
	}
	
	/* Downloads a File*/
	@GetMapping("/downloadpdf/{filename}")
	public ResponseEntity<byte[]> downloadPdf(@PathVariable String filename) throws IOException {
		switch (filename) {
			case "Elss Funds":
				generatePdfService.generateElssPdf();
				break;
			case "Summary":
				generatePdfService.generateSummaryPdf();
				break;
			case "Fd Details":
				generatePdfService.generateFdPdf();
				break;
		}
		byte[] bytes = Files.readAllBytes(ResourceUtils.getFile(StringUtils.join("classpath:generatedpdfs/",filename,".pdf" )).toPath());
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".pdf");
		
		return ResponseEntity.ok()
				.headers(headers)
				.body(bytes);
	}
	
	
	/* Endpoint for saving bank details and monthly expenditure and FDs*/
	@Transactional
	@GetMapping("/bulkupdate/bankdata")
	public String addStatementDetails()   {
		fetchBankDataService.fetchSbiStatementData();
		fetchBankDataService.fetchUbiStatementData();
		fetchBankDataService.fetchIciciStatementData();
		return  StringUtils.join(
					 fetchBankDataService.salaryUsage(),"\n",
					fetchBankDataService.collectFdDataofUBI(), '\n',
					fetchBankDataService.collectFdDataofSBI(), '\n',
					fetchBankDataService.expenditureDataofSBI(), '\n',
					fetchBankDataService.expenditureDataofUBI(), '\n',
					fetchBankDataService.saveBankDetailsofSBI(), '\n',
					fetchBankDataService.saveBankDetailsofUBI(), "\n",
					fetchBankDataService.saveBankDetailsofIcici()+ "\n",
				fetchBankDataService.expenditureDataofIcici()+ "\n",
				fetchBankDataService.yearlyExpenditure());
			 
	}
	@Transactional
	@GetMapping("/bulkupdate/mfstocksdata")
	public String addMfandStocksDetails()   {
		fetchMfStocksService.fetchStocksAndMfTransactionStatement();
		fetchMfStocksService.fetchStocksAndMfMetaData();
		return StringUtils.join(fetchMfStocksService.addElssStatementData(),'\n',
				fetchMfStocksService.saveStocks(),'\n',
				fetchMfStocksService.saveSipandMf(),'\n',
				fetchMfStocksService.saveAccountDetails()
				);
	}
}
