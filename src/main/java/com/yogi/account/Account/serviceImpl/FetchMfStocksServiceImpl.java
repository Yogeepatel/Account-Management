package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.ExcelReader.ExcelReader;
import com.yogi.account.Account.entity.BankDetails;
import com.yogi.account.Account.entity.ELSS;
import com.yogi.account.Account.entity.MutualFund;
import com.yogi.account.Account.entity.Sip;
import com.yogi.account.Account.pdfManipulation.CustomPdfBox;
import com.yogi.account.Account.pdfManipulation.PdfBox;
import com.yogi.account.Account.pdfManipulation.Tabula;
import com.yogi.account.Account.repository.BankDetailsRepositiory;
import com.yogi.account.Account.repository.ELSSRepository;
import com.yogi.account.Account.repository.MutualFundRepository;
import com.yogi.account.Account.repository.SipRepository;
import com.yogi.account.Account.service.FetchMfStocksService;
import com.yogi.account.Account.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class FetchMfStocksServiceImpl implements FetchMfStocksService {
	private Tabula tabula;
	private PdfBox pdfBox;
	private CustomPdfBox customPdfBox;
	private Environment environment;
	private SipRepository sipRepository;
	private ELSSRepository elssRepository;
	private MutualFundRepository mutualFundRepository;
	private BankDetailsRepositiory bankDetailsRepositiory;
	
	public static Map<String, String> accountDetails = new HashMap<>();
	public static Map<String, Map<String, String>> sipDeatils = new LinkedHashMap<>();
	public static Map<String, List<?>> mfFromPdf = new HashMap<>();
	public static Map<String, List<?>> stocksFromPdf = new LinkedHashMap<>();
	
	@Override
	public String addElssStatementData() {
		try {
			int count = 0;
			List<ELSS> retrievedData = elssRepository.findAll();
			List<String> folioNos = retrievedData.stream().map(obj -> obj.getFolio_number()).toList();
			if (!ObjectUtils.isEmpty(retrievedData)) {
				count = retrievedData.size();
			}
			
			PDDocument document = Loader.loadPDF(ResourceUtils.getFile("classpath:UplodedFiles/ELSS.pdf"));
			
			List<List<String>> result = tabula.pdfPartialTableReader(document, 1);
			
			for (int i = 1; i < result.size(); i++) {
				if (!folioNos.contains(result.get(i).get(1))) {
					ELSS elss = ELSS.builder().id(Integer.toString(i + count)).amount(result.get(i).get(4))
							.units(Double.parseDouble(result.get(i).get(3))).folio_number(result.get(i).get(1))
							.name_of_fund(result.get(i).get(2)).purchase_Date(result.get(i).get(0)).build();
					elssRepository.save(elss);
				}
			}
			return "Data Saved Successfully";
			
		} catch (Exception e) {
			return "Error Occuered while reading pdf " + e.getMessage();
		}
		
	}
	
	@Override
	public void fetchStocksAndMfMetaData() {
		PDDocument document;
		try {
			document = AccountUtils.copyDocument("src/main/resources/UplodedFiles/transaction-with-holding-statement_YCS402-1208160126402956 (2).pdf", environment.getProperty("zerodha.password"));
			
			List<String> stocksMfDataPdfPage2 = customPdfBox.fetchDocumentText(document, 2, 2);
			
			int i = 0;
			for (int j = 0; j < stocksMfDataPdfPage2.size() - 3; j++) {
				String data = stocksMfDataPdfPage2.get(j);
				String key = "";
				List<String> details = new ArrayList<>();
				for (String cell : data.split("\n")) {
					if (i == 0) {
						key = cell;
					} else if (i > 3 && i < 8) {
						i = i + 1;
						continue;
					} else if (i > 10) {
						i = i % 10;
						i = i - 1;
						String word = details.get(i);
						details.remove(i);
						word = StringUtils.join(word, cell);
						details.add(i, word);
					} else {
						details.add(cell);
					}
					i = i + 1;
				}
				if (key.contains("INF")) {
					mfFromPdf.put(key, details);
				} else if (key.contains("INE")) {
					stocksFromPdf.put(key, details);
				} else {
					stocksFromPdf.put(key, details);
					mfFromPdf.put(key, details);
				}
				i = 0;
			}
			
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public void fetchStocksAndMfTransactionStatement() {
		PDDocument document;
		try {
			document = AccountUtils.copyDocument("src/main/resources/UplodedFiles/transaction-with-holding-statement_YCS402-1208160126402956 (2).pdf", environment.getProperty("zerodha.password"));
			List<String> StocksMfTransactionStatment = customPdfBox.fetchDocumentText(document, 1, 1);
			int i = 0;
			int index = StocksMfTransactionStatment.indexOf("TRANSACTION WITH HOLDING STATEMENT") + 1;
			String data = StocksMfTransactionStatment.get(index);
			String key = "";
			String details = "";
			for (String cell : data.substring(4).split("\n")) {
				if (i == 0 && !cell.contains(":")) {
					accountDetails.putIfAbsent("Address: ", details);
					details = accountDetails.get("Address: ");
					details = StringUtils.join(details, cell);
					accountDetails.put("Address: ", details);
					details = "";
					i = i + 1;
				} else {
					if (cell.contains(":")) {
						key = cell;
						i = i + 1;
					} else {
						details = cell;
						accountDetails.put(key, details);
						i = 0;
						key = "";
						details = "";
					}
				}
			}
			
			int statementDataIndex = StocksMfTransactionStatment.lastIndexOf("Statement of Account from 2024-03-01 to 2024-03-31:");
			
			List<String> statementDataList = StocksMfTransactionStatment.subList(statementDataIndex + 2, StocksMfTransactionStatment.size()).stream().filter(val -> val.contains(":")).toList();
			i = 0;
			key = "";
			String value = "";
			Map<String, String> temp = new HashMap<>();
			List<Map<String, String>> templist = new ArrayList<>();
			for (String word : statementDataList) {
				if (i == 0) {
					String[] words = word.split(": ");
					if (words.length == 3) {
						key = words[1].split(" ")[0];
						temp.put(words[1].split(" ")[1], words[2]);
					}
				} else {
					String[] words = word.split("\n");
					temp.put(words[0], words[1]);
				}
				if (i == 2) {
					sipDeatils.put(key, temp);
					temp = new HashMap<>();
				}
				i = (i + 1) % 3;
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	@Override
	public void saveSipandMf() {
		try {
			FileInputStream file = new FileInputStream(new File("src/main/resources/UplodedFiles/holdings.xlsx"));
			Map<String, List<?>> mfMetadataExel = ExcelReader.readExcel(file, 1);
			fetchStocksAndMfMetaData();
			fetchStocksAndMfTransactionStatement();
			mfMetadataExel.forEach((key, value) -> {
				if (sipDeatils.containsKey(value.get(0))) {
					if (Double.parseDouble((String) mfFromPdf.get(value.get(0)).get(3)) != 0.0) {
						List<ELSS> elssData = elssRepository.findAll();
						Optional<ELSS> elss = elssData.stream().filter(id -> id.getId().equalsIgnoreCase(String.valueOf(value.get(0)))).findFirst();
						if (elss.isPresent() && StringUtils.isBlank(elss.get().getIsinCode())) {
							elssRepository.updateIsinCode(String.valueOf(value.get(0)), elss.get().getUnits());
						}
					} else {
						if (ObjectUtils.isEmpty(sipRepository.findById(String.valueOf(value.get(0))))) {
							Map<String, String> sipdata = sipDeatils.get(value.get(0));
							List<?> pdfData = mfFromPdf.get(value.get(0));
							Sip sipEntity = Sip.builder().id(String.valueOf(value.get(0))).companyName(sipdata.get("Symbol"))
									.sipName(key).currentBalance(Double.parseDouble(String.valueOf(pdfData.get(1)))).freeBalance(Double.parseDouble(String.valueOf(pdfData.get(2))))
									.rate(Double.parseDouble((String) pdfData.get(4))).amount(Double.parseDouble((String) pdfData.get(5))).monthlyOpeningBalance(Double.parseDouble(sipdata.get("Opening balance:")))
									.monthlyClosingBalance(Double.parseDouble(sipdata.get("Closing balance:"))).build();
							sipRepository.save(sipEntity);
						}
					}
				} else {
					if (ObjectUtils.isEmpty(mutualFundRepository.findById(String.valueOf(value.get(0))))) {
						List<?> pdfData = mfFromPdf.get(value.get(0));
						if (ObjectUtils.isNotEmpty(pdfData)) {
							System.out.println(String.valueOf(value.get(0)));
							MutualFund mutualFund = MutualFund.builder().id(String.valueOf(value.get(0))).companyName((String) pdfData.get(0))
									.mutualFundName(key).currentBalance(Double.parseDouble(String.valueOf(pdfData.get(1)))).freeBalance(Double.parseDouble(String.valueOf(pdfData.get(2))))
									.rate(Double.parseDouble((String) pdfData.get(4))).amount(Double.parseDouble((String) pdfData.get(5))).build();
							mutualFundRepository.save(mutualFund);
						}
					}
				}
			});
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void saveAccountDetails() {
		fetchStocksAndMfTransactionStatement();
		List<String> mapKeys = accountDetails.keySet().stream().toList();
		BankDetails accountDetail = BankDetails.builder().accountNumber(accountDetails.get(mapKeys.get(5)))
				.account_Status(accountDetails.get(accountDetails.get(4))).id("Zerodha")
				.bo_Status(accountDetails.get(mapKeys.get(7))).pan_Number(accountDetails.get(mapKeys.get(9)))
				.client_Id(accountDetails.get(mapKeys.get(1))).dp_Id(accountDetails.get(mapKeys.get(10)))
				.password(accountDetails.get(mapKeys.get(9))).address(accountDetails.get(mapKeys.get(6))).build();
		
		System.out.println(accountDetails.get(mapKeys.get(0)));
		bankDetailsRepositiory.save(accountDetail);
		System.out.println(accountDetails);
	}
	
}
