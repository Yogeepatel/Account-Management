package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.ExcelReader.ExcelReader;
import com.yogi.account.Account.entity.*;
import com.yogi.account.Account.pdfManipulation.CustomPdfBox;
import com.yogi.account.Account.pdfManipulation.Tabula;
import com.yogi.account.Account.repository.*;
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
	private CustomPdfBox customPdfBox;
	private Environment environment;
	private SipRepository sipRepository;
	private ELSSRepository elssRepository;
	private MutualFundRepository mutualFundRepository;
	private BankDetailsRepositiory bankDetailsRepositiory;
	private StocksRepository stocksRepository;
	
	public static Map<String, String> accountDetails = new HashMap<>();
	public static Map<String, Map<String, String>> sipDeatils = new LinkedHashMap<>();
	public static Map<String, List<?>> mfFromPdf = new HashMap<>();
	public static Map<String, List<?>> stocksFromPdf = new LinkedHashMap<>();
	
	@Override
	public String addElssStatementData() {
		try {
			int count = 0;
			List<ELSS> retrievedData = elssRepository.findAll();
			List<String> folioNos = retrievedData.stream().map(ELSS::getFolioNumber).toList();
			if (!ObjectUtils.isEmpty(retrievedData)) {
				count = retrievedData.size();
			}
			
			PDDocument document = Loader.loadPDF(ResourceUtils.getFile("classpath:UplodedFiles/ELSS.pdf"));
			
			List<List<String>> result = tabula.pdfPartialTableReader(document, 1);
			
			for (int i = 1; i < result.size(); i++) {
				if (!folioNos.contains(result.get(i).get(1))) {
					ELSS elss = ELSS.builder().id(Integer.toString(i + count)).amount(result.get(i).get(4))
							.units(Double.parseDouble(result.get(i).get(3))).folioNumber(result.get(i).get(1))
							.nameOfFund(result.get(i).get(2)).purchaseDate(result.get(i).get(0)).build();
					elssRepository.save(elss);
				}
			}
			return "Data Saved Successfully";
			
		} catch (Exception e) {
			return "Error Occuered while reading pdf " + e.getMessage();
		}
		
	}
	

	private void fetchStocksAndMfMetaData() {
		PDDocument document;
		try {
			document = AccountUtils.copyDocument("src/main/resources/UplodedFiles/Holding transaction Statement.pdf", environment.getProperty("pan"));
			
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
	
	private  void fetchStocksAndMfTransactionStatement() {
		PDDocument document;
		try {
			document = AccountUtils.copyDocument("classpath:UplodedFiles/Holding transaction Statement.pdf", environment.getProperty("pan"));
			List<String> StocksMfTransactionStatment = customPdfBox.fetchDocumentText(document, 1, 1);
			int i = 0;
			int index = StocksMfTransactionStatment.indexOf("TRANSACTION WITH HOLDING STATEMENT") + 1;
			String data = StocksMfTransactionStatment.get(index);
			String key = "";
			String details = "";
			String[] dataArray = data.substring(4).split("\n");
			while(i< dataArray.length){
				
				if (dataArray[i].contains(":")) {
					
					if(dataArray[i].split(":").length > 1) {
						key = dataArray[i].split(":")[0];
						details = dataArray[i].split(":")[1];
						accountDetails.put(key, details);
						i=i+1;
					}

					else{
						key = dataArray[i];
						details = dataArray[i + 1];
						accountDetails.put(key, details);
						i= i+2;
					}
//
				} else {
					
					details="";
					
					details =accountDetails.putIfAbsent("Address: ", details);
					if(ObjectUtils.isNotEmpty(details) && details.equals(dataArray[i])) {
						continue;
					}
					details = StringUtils.join(details, dataArray[i]);
					accountDetails.put("Address: ", details);
					
					i = i + 1;
					}
				key = "";
				details = "";
				
				}
			int statementDataIndex = StocksMfTransactionStatment.lastIndexOf("Statement of Account from 2024-03-01 to 2024-03-31:");
			
			List<String> statementDataList = StocksMfTransactionStatment.subList(statementDataIndex + 2, StocksMfTransactionStatment.size()).stream().filter(val -> val.contains(":")).toList();
			i = 0;
			key = "";
			Map<String, String> temp = new HashMap<>();
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
	public void generateStocksMfData(){
		fetchStocksAndMfMetaData();
		fetchStocksAndMfTransactionStatement();
		
	}
	
	@Override
	public String saveSipandMf() {
		try {
			FileInputStream file = new FileInputStream(ResourceUtils.getFile("classpath:UplodedFiles/holdings.xlsx"));
			Map<String, List<?>> mfMetadataExel = ExcelReader.readExcel(file, 1);
			mfMetadataExel.forEach((key, value) -> {
				if (mfFromPdf.containsKey(value.get(0))&& Double.parseDouble((String) mfFromPdf.get(value.get(0)).get(3)) != 0.0){
					if ( ObjectUtils.isNotEmpty(mfFromPdf.get(value.get(0)) )&& elssRepository.existByUnits( Double.parseDouble((String) mfFromPdf.get(value.get(0)).get(3))).getUnits() == Double.parseDouble((String) mfFromPdf.get(value.get(0)).get(3))) {
						ELSS elssData = elssRepository.existByUnits(Double.parseDouble((String) mfFromPdf.get(value.get(0)).get(3)));
						elssRepository.delete(elssData);
						elssData.setId(String.valueOf(value.get(0)));
						elssRepository.save(elssData);
					}
				}
				else if (sipDeatils.containsKey(value.get(0))) {
						if (Double.parseDouble(sipDeatils.get(String.valueOf(value.get(0))).get("Closing balance:")) - Double.parseDouble(sipDeatils.get(String.valueOf(value.get(0))).get("Opening balance:") )>0)
							if (ObjectUtils.isEmpty(sipRepository.findById(String.valueOf(value.get(0))))) {
							Map<String, String> sipdata = sipDeatils.get(value.get(0));
							List<?> pdfData = mfFromPdf.get(value.get(0));
							
							Sip sipEntity = Sip.builder().id(String.valueOf(value.get(0))).companyName(sipdata.get("Symbol")).fundType((String) value.get(1))
									.sipName(key).currentBalance(Double.parseDouble(String.valueOf(pdfData.get(1)))).freeBalance(Double.parseDouble(String.valueOf(pdfData.get(2))))
									.rate(Double.parseDouble((String) pdfData.get(4))).amount(Double.parseDouble((String) pdfData.get(5))).monthlyOpeningBalance(Double.parseDouble(sipdata.get("Opening balance:")))
									.monthlyClosingBalance(Double.parseDouble(sipdata.get("Closing balance:"))).build();
							sipRepository.save(sipEntity);
							try {
								mutualFundRepository.deleteById(String.valueOf(value.get(0)));
							}catch (Exception e){
								throw new RuntimeException("Mutual Fund is not present");
							}
						}
					}
				 else {
						if (ObjectUtils.isEmpty(mutualFundRepository.findById(String.valueOf(value.get(0)))) &&
								ObjectUtils.isEmpty(elssRepository.findById(String.valueOf(value.get(0)))) &&
								ObjectUtils.isEmpty(sipRepository.findById(String.valueOf(value.get(0))))) {
							List<?> pdfData = mfFromPdf.get(value.get(0));
							if (ObjectUtils.isNotEmpty(pdfData)) {
								MutualFund mutualFund = MutualFund.builder().id(String.valueOf(value.get(0))).companyName((String) pdfData.get(0))
										.mutualFundName(key).currentBalance(Double.parseDouble(String.valueOf(pdfData.get(1)))).freeBalance(Double.parseDouble(String.valueOf(pdfData.get(2))))
										.rate(Double.parseDouble((String) pdfData.get(4))).fundType((String) value.get(1)).amount(Double.parseDouble((String) pdfData.get(5))).build();
								mutualFundRepository.save(mutualFund);
							}
						}
					}
				
			});
			return "All Mutual Funds and SIP details updated and saved";
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String saveAccountDetails() {
		
		List<String> mapKeys = accountDetails.keySet().stream().toList();
		String address = StringUtils.join(accountDetails.get(mapKeys.get(9)).split("Patel")[1],accountDetails.get(mapKeys.get(6)).split("PATEL")[1]);
		String name=accountDetails.get(mapKeys.get(6)).split("PATEL")[0] + "PATEL";
		BankDetails savedAccount = bankDetailsRepositiory.findAll().stream().filter(account -> !account.equals("Zerodha")).findFirst().orElseThrow();
		
		BankDetails accountDetail   = BankDetails.builder().name(name.substring(1)).accountNumber(accountDetails.get(mapKeys.get(5)) != null?accountDetails.get(mapKeys.get(5)) :savedAccount.getAccountNumber())
				.accountStatus(accountDetails.get(mapKeys.get(4))!= null?accountDetails.get(mapKeys.get(4)) :savedAccount.getAccountStatus()).id("Zerodha")
				.boStatus(accountDetails.get(mapKeys.get(3))!= null?accountDetails.get(mapKeys.get(3)) :savedAccount.getBoStatus()).panNumber(accountDetails.get(mapKeys.get(10))!= null?accountDetails.get(mapKeys.get(10)) :savedAccount.getPanNumber())
				.customerId(accountDetails.get(mapKeys.get(1))!= null?accountDetails.get(mapKeys.get(1)) :savedAccount.getCustomerId()).dpId(accountDetails.get(mapKeys.get(11))!= null?accountDetails.get(mapKeys.get(11)) :savedAccount.getDpId())
				.password(accountDetails.get(mapKeys.get(10)) != null?accountDetails.get(mapKeys.get(10)) :savedAccount.getPanNumber()).address(address!=null?address :savedAccount.getAddress()).build();
		
		bankDetailsRepositiory.save(accountDetail);
		return "Account details updated";
	}
	
	@Override
	public String saveStocks(){
		try {
			FileInputStream file = new FileInputStream(new File("src/main/resources/UplodedFiles/holdings.xlsx"));
			
			Map<String, List<?>> stocksDataFromExcel  = ExcelReader.readExcel(file, 0);
			stocksDataFromExcel.forEach((key,value) ->{
				if (stocksFromPdf.containsKey(value.get(0))){
					List stockDatalist = stocksFromPdf.get(value.get(0));
					Optional<Stocks> stocksData= stocksRepository.findById(String.valueOf(value.get(0)));
					if (stocksData.isPresent() && stocksData.get().getCurrentBalance() != Double.parseDouble((String) stockDatalist.get(2))){
						stocksData.get().setCurrentBalance(Double.parseDouble((String) stockDatalist.get(2)));
						stocksRepository.updateUnits(String.valueOf(value.get(0)),Double.parseDouble((String) stockDatalist.get(1)));
						
					}
					else{
						Stocks stock = new Stocks(String.valueOf(value.get(0)),String.valueOf(stockDatalist.get(0)),key,String.valueOf(value.get(1)),Double.parseDouble((String) stockDatalist.get(1)),Double.parseDouble((String) stockDatalist.get(4)),Double.parseDouble((String) stockDatalist.get(5)));
						stocksRepository.save(stock);
					}
				}
			});
		return "Stocks data updated and saved.";
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
	}
}
