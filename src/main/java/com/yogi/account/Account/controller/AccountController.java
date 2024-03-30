package com.yogi.account.Account.controller;

import com.yogi.account.Account.entity.LoginDto;
import com.yogi.account.Account.service.FetchBankDataService;
import com.yogi.account.Account.service.FetchMfStocksService;
import com.yogi.account.Account.service.GeneratePdfService;
import com.yogi.account.Account.service.PasswordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;


@AllArgsConstructor
@RestController
public class AccountController {
	private GeneratePdfService generatePdfService;
	private FetchMfStocksService FetchMfStocksService;
	private FetchBankDataService fetchBankDataService;
	private PasswordService passwordService;
	
	
	@GetMapping("/login")
	public String login(@RequestBody LoginDto loginDto) {
		System.out.println(loginDto);
		return passwordService.login(loginDto.getUsername(), loginDto.getPassword());
	}
	
	@GetMapping("/key/{key}")
	public String validateKey(@PathVariable String key) {
		return passwordService.validateKey(key);
	}
	
	@GetMapping("/passwordString")
	public String passwordString() {
		return (String) passwordService.getpasswordhelper();
	}
	
	@GetMapping("/uploadpdf/{filename}")
	public String uploadpdf(@PathVariable String filename, @RequestBody byte[] filecontent) {
		try (FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/UplodedFiles" + filename)) {
			fileOutputStream.write(filecontent);
			
			return "PDF file saved successfully.";
		} catch (Exception e) {
			return ("Error saving PDF file: " + e.getMessage());
			
		}
	}
	
	@GetMapping("/uploadpdf")
	public String uploadpdfs(@RequestParam String filename, @RequestBody byte[] filecontent) {
		try (FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/UplodedFiles" + filename)) {
			fileOutputStream.write(filecontent);
			
			return "PDF file saved successfully.";
		} catch (Exception e) {
			return ("Error saving PDF file: " + e.getMessage());
			
		}
	}
	
	@GetMapping("/downloadpdf")
	public ResponseEntity<byte[]> downloadPdf(@PathVariable String filename) throws IOException {
		byte[] bytes = generatePdfService.drawSummary();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".pdf");
		
		return ResponseEntity.ok()
				.headers(headers)
				.body(bytes);
	}
	
	@GetMapping("/elss")
	public String elss() throws Exception {
		return FetchMfStocksService.addElssStatementData();
	}
	
	@GetMapping("/addDetails")
	public String addStatementDetails(@RequestParam String task) throws Exception {
		String responseSbiService = fetchBankDataService.fetchSbiStatementData(task);
		String responseUbiService = fetchBankDataService.fetchSbiStatementData(task);
		return responseSbiService + "\n " + responseUbiService;
	}
}
