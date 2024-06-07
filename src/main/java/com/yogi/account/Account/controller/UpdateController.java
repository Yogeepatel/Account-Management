package com.yogi.account.Account.controller;

import com.yogi.account.Account.dto.UpdateRequestDto;
import com.yogi.account.Account.entity.UploadMetaData;
import com.yogi.account.Account.repository.FileUploadMetaDataRepository;
import com.yogi.account.Account.service.FetchBankDataService;
import com.yogi.account.Account.service.FetchMfStocksService;
import com.yogi.account.Account.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/update")
public class UpdateController {
	private FetchMfStocksService fetchMfStocksService;
	private FetchBankDataService fetchBankDataService;
	private FileUploadMetaDataRepository fileUploadMetaDataRepository;
	
	
	private String updateStocks(Map<String, String> fileNameMap) {
		System.out.println("in stocks");
		try {
			fetchMfStocksService.readstocksCsvFile((fileNameMap.get("Upload All Holdings Csv File")));
			fetchMfStocksService.fetchStocksAndMfMetaData(fileNameMap.get("Upload All Holding Pdf File"));
			return fetchMfStocksService.saveStocks();
		} catch (Exception e) {
			return "Failed to Update Stocks Data. Error: " + e.getMessage();
		}
	}
	
	private String updateSipandMf(Map<String, String> fileNameMap) {
		try {
			fetchMfStocksService.readMfCsvFile((fileNameMap.get("Upload All Holdings Csv File")));
			fetchMfStocksService.fetchStocksAndMfTransactionStatement(fileNameMap.get("Upload All Holding Pdf File"));
			fetchMfStocksService.fetchStocksAndMfMetaData(fileNameMap.get("Upload All Holding Pdf File"));
			return fetchMfStocksService.saveSipandMf();
		} catch (Exception e) {
			return "Failed to Update SIP/Mutual fund Data. Error: " + e.getMessage();
		}
	}
	
	private String updateAccountDetails(Map<String, String> fileNameMap) {
		try {
			fetchBankDataService.fetchSbiStatementData(fileNameMap.get("Upload SBI Statement Pdf File"));
			System.out.println("sbi comp");
			fetchBankDataService.fetchUbiStatementData(fileNameMap.get("Upload UBI Statement Pdf File"));
			System.out.println("ubi comp");
			fetchBankDataService.fetchIciciStatementData(fileNameMap.get("Upload ICICI Statement Non-Tabulated Pdf File"), null);
			System.out.println("icici comp");
			fetchMfStocksService.fetchStocksAndMfTransactionStatement(fileNameMap.get("Upload Zerodha Statement Pdf File"));
			return StringUtils.join(fetchBankDataService.saveBankDetailsofSBI(), '\n',
					fetchBankDataService.saveBankDetailsofUBI(), "\n",
					fetchBankDataService.saveBankDetailsofIcici() + "\n", fetchMfStocksService.saveAccountDetails());
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to Update Account details. Error: " + e.getMessage();
		}
	}
	
	private String updateFd(Map<String, String> fileNameMap) {
		try {
			fetchBankDataService.fetchSbiStatementData(fileNameMap.get("Upload SBI Statement Pdf File"));
			System.out.println("sbi comp");
			fetchBankDataService.fetchUbiStatementData(fileNameMap.get("Upload UBI Statement Pdf File"));
			System.out.println("ubi comp");
			fetchBankDataService.fetchIciciStatementData(fileNameMap.get("Upload ICICI Statement Pdf File"), null);
			System.out.println("icici comp");
			return StringUtils.join(fetchBankDataService.collectFdDataofSBI(), '\n',
					fetchBankDataService.collectFdDataofUBI(), "\n");
		} catch (Exception e) {
			return "Failed to Update Fd Data. Error: " + e.getMessage();
		}
	}
	
	private String updateElss(Map<String, String> fileNameMap) {
		try {
			return fetchMfStocksService.addElssStatementData(fileNameMap.get("Upload ELSS Holding Pdf File"));
		} catch (Exception e) {
			return "Failed to Update ELSS Data. Error: " + e.getMessage();
		}
	}
	
	private String monthlyExpenditure(Map<String, String> fileNameMap, String month, int expenditureButton) {
		System.out.println(fileNameMap);
		System.out.println(fileNameMap.get("Upload Tabulated ICICI Pdf Statement"));
		try {
			if (expenditureButton == 1) {
				fetchBankDataService.fetchIciciStatementData(fileNameMap.get("Upload Tabulated ICICI Pdf Statement"), fileNameMap.get("Upload Non-Tabulated ICICI Pdf Statement"));
				fetchBankDataService.fetchSbiStatementData(fileNameMap.get("Upload SBI Pdf Statement"));
				fetchBankDataService.fetchUbiStatementData(fileNameMap.get("Upload UBI Pdf Statement"));
				return StringUtils.join(
						fetchBankDataService.expenditureDataofUBI(month), "\n",
						fetchBankDataService.expenditureDataofIcici(month), "\n",
						fetchBankDataService.expenditureDataofSBI(month)
				);
			} else if (expenditureButton == 2) {
				Set<String> keys = fileNameMap.keySet();
				for (String key : keys) {
					if (key.toLowerCase().contains("sbi")) {
						fetchBankDataService.fetchSbiStatementData(fileNameMap.get("Upload SBI Pdf Statement"));
						return fetchBankDataService.expenditureDataofSBI(month);
					}
					if (key.toLowerCase().contains("ubi")) {
						fetchBankDataService.fetchUbiStatementData(fileNameMap.get("Upload UBI Pdf Statement"));
						return fetchBankDataService.expenditureDataofUBI(month);
					}
					if (key.toLowerCase().contains("icici")) {
						fetchBankDataService.fetchIciciStatementData(fileNameMap.get("Upload Tabulated ICICI Pdf Statement"), fileNameMap.get("Upload Non-Tabulated ICICI Pdf Statement"));
						return fetchBankDataService.expenditureDataofIcici(month);
					}
				}
			}
			return "Failed to Update Monthly Expenditure Data.";
			
		} catch (Exception e) {
			return "Failed to Update Monthly Expenditure Data. Error: " + e.getMessage();
		}
	}
	
	private String updateSalaryUsage(Map<String, String> fileNameMap, String month) {
		fetchBankDataService.fetchIciciStatementData(fileNameMap.get("Upload Tabulated ICICI Pdf Statement"), fileNameMap.get("Upload Non-Tabulated ICICI Pdf Statement"));
		
		return fetchBankDataService.salaryUsage(month);
	}
	
	@PostMapping()
	public String updateRequest(@RequestParam String type, @RequestBody UpdateRequestDto filesname) {
		Optional<UploadMetaData> optional = fileUploadMetaDataRepository.findById(type);
		if (optional.isEmpty()) {
			throw new RuntimeException("File Format meta data not found.");
		}
		UploadMetaData uploadMetaData = optional.get();
		if (filesname.getFilesNameMap().size() != uploadMetaData.getAttachmentTitle().keySet().size()) {
			throw new RuntimeException("All required file not uploaded.");
		}
		Map<String, String> fileNameMap = filesname.getFilesNameMap();
		for (String key : fileNameMap.keySet()) {
			String path = type + "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy  ")) + fileNameMap.get(key);
			fileNameMap.replace(key, path);
		}
		switch (type) {
			case "accountdetails" -> {
				return updateAccountDetails(fileNameMap);
			}
			case "stocks" -> {
				return updateStocks(fileNameMap);
			}
			case "elss" -> {
				return updateElss(fileNameMap);
			}
			case "mutualfund", "sip" -> {
				return updateSipandMf(fileNameMap);
			}
			case "fd" -> {
				return updateFd(fileNameMap);
			}
		}
		return "Invalid page to update. Please check again.";
	}
	
	@PostMapping("/monthDependent")
	public Object monthDependentUpdateRequest(@RequestParam String type, @RequestBody UpdateRequestDto filesname, @RequestParam String month, @RequestParam int expenditureButton) {
		Map<String, String> fileNameMap = filesname.getFilesNameMap();
		for (String key : fileNameMap.keySet()) {
			String path = "expenditure/" + type + "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy  ")) + fileNameMap.get(key);
			fileNameMap.replace(key, path);
		}
		switch (type) {
			case "salaryUsage" -> {
				filevalidation(filesname, AccountUtils.salaryUsageFileUploadMetaData());
				return updateSalaryUsage(fileNameMap, month);
			}
			case "monthly" -> {
				if (expenditureButton == 1) {
					filevalidation(filesname, AccountUtils.monthExpenditureFileUploadMetaData());
					return monthlyExpenditure(fileNameMap, month, expenditureButton);
				}
				
				if (expenditureButton == 2) {
					filevalidation(filesname, AccountUtils.accountExpenditureFileUploadMetaData(filesname.getAttachmentTitleKey()));
				}
				return monthlyExpenditure(fileNameMap, month, expenditureButton);
				
			}
		}
		return "Invalid page to update. Please check again.";
		
		
	}
	
	private void filevalidation(UpdateRequestDto filesname, UploadMetaData uploadMetaData) {
		System.out.println(filesname.getFilesNameMap());
		System.out.println(uploadMetaData.getAttachmentTitle());
		if (filesname.getFilesNameMap().size() != uploadMetaData.getAttachmentTitle().keySet().size()) {
			throw new RuntimeException("All required file not uploaded.");
		}
		
	}
}
