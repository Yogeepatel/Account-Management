package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.constants.AccountConstants;
import com.yogi.account.Account.constants.FdEnums;
import com.yogi.account.Account.entity.*;
import com.yogi.account.Account.pdfManipulation.PdfBox;
import com.yogi.account.Account.pdfManipulation.Tabula;
import com.yogi.account.Account.repository.*;
import com.yogi.account.Account.service.FetchBankDataService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FetchBankDataServiceImpl implements FetchBankDataService {
    private Tabula tabula;
    private PdfBox pdfBox;
    private BankDetailsRepositiory bankDetailsRepositiory;
    private FDRepository fdRepository;
    private AccountExpenditureRepository bankAccountExpenditureRepositiory;
    private SalaryUsageRepository salaryUsageRepository;
    private YearlyExpenditureRepository yearlyExpenditureRepository;
    
    public static List<List<String>> firstPageTableDataUbi = null;
    public static List<List<String>> lastPageTableDataUbi = null;
    public static String page4TextSbi;
    public static String page4TextSortedSbi;
    public static List<List<String>> page4TableDataSbi;
    public static List<List<String>> page3TableDataSbi;
    public static String textLastPageUbi = null;
    public static String page1TextIcici;
    public static String page2TextIcici;
    public static List<List<String>> tabularIciciStatementdata;
    
    public static String month = LocalDate.now().minusMonths(1).getMonth().name();
    
    @Autowired
    public Environment environment;
    

    public void fetchSbiStatementData(String filename ) {
        
        PDDocument document;
        PDDocument document2;
        String password = "SBI.password";
        try {
            document = Loader.loadPDF(ResourceUtils.getFile("src/main/resources/UploadedFiles/"+filename), environment.getProperty(password));
            page4TableDataSbi = tabula.pdfTableReader(document, document.getNumberOfPages());
            page3TableDataSbi = tabula.pdfTableReaderWithRuling(document, document.getNumberOfPages() - 1);
            document = Loader.loadPDF(ResourceUtils.getFile("src/main/resources/UploadedFiles/"+filename), environment.getProperty(password));
            document2 = Loader.loadPDF(ResourceUtils.getFile("src/main/resources/UploadedFiles/"+filename), environment.getProperty(password));
            
            page4TextSbi = pdfBox.read(document, document.getNumberOfPages(), false);
            page4TextSortedSbi = pdfBox.read(document2, document.getNumberOfPages(), true);
        }catch (InvalidPasswordException e) {
            e.printStackTrace();
            throw new RuntimeException("wrong File Uploaded for"+ "SBI Bank.");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while reading SBI statement pdf " + e.getMessage());
        }
        if (ObjectUtils.isEmpty(document) || CollectionUtils.isEmpty(page4TableDataSbi) || StringUtils.isBlank(page4TextSbi) || CollectionUtils.isEmpty(page3TableDataSbi)) {
            throw new RuntimeException("Error occurred while reading SBI statement pdf ");
        }
        
        
    }
    

    public void
    fetchIciciStatementData(String tabularFileName, String nonTabularFileName) {
        String password = "ICICI.password";
        try {
            
            if(ObjectUtils.isNotEmpty(nonTabularFileName)) {
                PDDocument pdDocument1 = Loader.loadPDF(ResourceUtils.getFile("src/main/resources/UploadedFiles/" + nonTabularFileName), environment.getProperty(password));
                page1TextIcici = pdfBox.read(pdDocument1, 1, false);
                PDDocument document = Loader.loadPDF(ResourceUtils.getFile("src/main/resources/UploadedFiles/" + nonTabularFileName), environment.getProperty(password));
                page2TextIcici = pdfBox.read(document, document.getNumberOfPages(), false);
            }
           
            if(ObjectUtils.isNotEmpty(tabularFileName)) {
                PDDocument pdDocument = Loader.loadPDF(ResourceUtils.getFile("src/main/resources/UploadedFiles/" + tabularFileName),environment.getProperty(password));
                int page = 2;
                List<List<String>> dataList = tabula.pdfTableReader(pdDocument, 1);
                while (page <= pdDocument.getNumberOfPages()) {
                    pdDocument = Loader.loadPDF(ResourceUtils.getFile("src/main/resources/UploadedFiles/" + tabularFileName));
                    List<List<String>> dataListTemp = tabula.pdfTableReader(pdDocument, page);
                    dataList.addAll(dataListTemp);
                    page = page + 1;
                }
                tabularIciciStatementdata = new ArrayList<>(dataList);
            }
        }catch (InvalidPasswordException e) {
            e.printStackTrace();
            throw new RuntimeException("wrong File Uploaded for "+ "ICICI Bank.");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while loading ICICI statement pdf " + e.getMessage());
        }
        if (ObjectUtils.isNotEmpty(tabularFileName) && (ObjectUtils.isEmpty(tabularIciciStatementdata) || CollectionUtils.isEmpty(tabularIciciStatementdata) )|| (ObjectUtils.isNotEmpty(nonTabularFileName) && (StringUtils.isBlank(page2TextIcici) || StringUtils.isBlank(page1TextIcici)))) {
            
            throw new RuntimeException("Error occurred while reading ICICI statement pdf ");
        }
    }
	   

    public void fetchUbiStatementData(String filename) {
        
        PDDocument document = null;
        String password = "UBI.password";
        try {
            document = Loader.loadPDF(ResourceUtils.getFile("src/main/resources/UploadedFiles/"+filename), environment.getProperty(password));
            firstPageTableDataUbi = tabula.pdfTableReader(document, 1);
            lastPageTableDataUbi = tabula.pdfTableReader(document, document.getNumberOfPages() - 1);
            textLastPageUbi = pdfBox.read(document, document.getNumberOfPages() - 1, true);
        }catch (InvalidPasswordException e) {
            e.printStackTrace();
            throw new RuntimeException("wrong File Uploaded for"+ "UBI Bank.");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while reading UBI statement pdf " + e.getMessage());
        }
        if (ObjectUtils.isEmpty(document) || CollectionUtils.isEmpty(firstPageTableDataUbi) || StringUtils.isBlank(textLastPageUbi) || CollectionUtils.isEmpty(lastPageTableDataUbi)) {
            throw new RuntimeException("Error occurred while reading UBI statement pdf");
        }
        
    }
    

    public String saveBankDetailsofUBI() {
        
        try {
            List<String> bankDetailsTableList = new BankDetails().bankDetailsTableList();
            firstPageTableDataUbi = firstPageTableDataUbi.subList(0, bankDetailsTableList.size() - 1);
            Map<String, String> bankDetailsMapperTable = new HashMap<>();
            for (int i = 0; i < bankDetailsTableList.size() - 1; i++) {
                for (int j = 0; j < firstPageTableDataUbi.size(); j++) {
                    for (int k = 0; k < firstPageTableDataUbi.get(j).size() - 1; k++) {
                        if (!StringUtils.isBlank(firstPageTableDataUbi.get(j).get(k)) && (firstPageTableDataUbi.get(j).get(k).toLowerCase().split(":")[0].trim().contains(bankDetailsTableList.get(i).toLowerCase()) || bankDetailsTableList.get(i).toLowerCase().trim().contains(firstPageTableDataUbi.get(j).get(k).toLowerCase().split(":")[0].trim()))) {
                            String[] spliyararay = firstPageTableDataUbi.get(j).get(k).split("&");
                            if (spliyararay.length > 1) {
                                if (spliyararay[0].toLowerCase().trim().equalsIgnoreCase(bankDetailsTableList.get(i).toLowerCase())) {
                                    bankDetailsMapperTable.put(bankDetailsTableList.get(i), firstPageTableDataUbi.get(j + 1).get(0));
                                }
                                if (spliyararay[1].trim().toLowerCase().contains(bankDetailsTableList.get(i).toLowerCase())) {
                                    String address = "";
                                    for (int s = 2; s < firstPageTableDataUbi.size(); s++) {
                                        address = StringUtils.join(address, firstPageTableDataUbi.get(s).get(0), " ");
                                    }
                                    bankDetailsMapperTable.put(bankDetailsTableList.get(i), address);
                                }
                            } else {
                                if (!StringUtils.isBlank(firstPageTableDataUbi.get(j).get(k + 1))) {
                                    bankDetailsMapperTable.put(bankDetailsTableList.get(i), firstPageTableDataUbi.get(j).get(k + 1));
                                }
                            }
                            
                        }
                    }
                }
            }
            String password = "UBI.password";
            BankDetails bankDetails = BankDetails.builder().id(AccountConstants.UBI).accountNumber(bankDetailsMapperTable.get("Account Number"))
                    .address(bankDetailsMapperTable.get("address")).micrCode(bankDetailsMapperTable.get("micr"))
                    .name(bankDetailsMapperTable.get("name")).ifscCode(bankDetailsMapperTable.get("ifsc")).customerId(bankDetailsMapperTable.get("Customer ID"))
                    .branch(bankDetailsMapperTable.get("branch name")).password(environment.getProperty(password))
                    .build();
            bankDetailsRepositiory.save(bankDetails);
            return "UBI bank details saved successfully";
        } catch (Exception e) {
            return "Error occurred while saving UBI bank details";
        }
        
    }
    
    
    
    

    public String saveBankDetailsofIcici() {
        String[] page1TextArray = page1TextIcici.split("\n");
        String fd = "";
        String address = "";
        String name = "";
        String branch = "";
        String custId = "";
        String accountNumber = "";
        
        String micr = "";
        String ifsc = "";
        int count = 0;
        for (int i = 0; i < page1TextArray.length; i++) {
            if (page1TextArray[i].contains("ID:")) {
                custId = page1TextArray[i].trim().split("ID:")[1].trim().split(" ")[0];
            }
            if (page1TextArray[i].contains("ACCOUNT DETAILS")) {
                String[] value = page1TextArray[i + 2].split(" ");
                accountNumber = value[2];
                
                i = i + 2;
            }
            if (page1TextArray[i].contains("YOGENDRA")) {
                name = page1TextArray[i].trim();
                count = 4;
                while (count > 0) {
                    i = i + 1;
                    address = StringUtils.join(address, " ", page1TextArray[i].trim());
                    count = count - 1;
                }
            }
            if (page1TextArray[i].contains("Base Branch")) {
                branch = StringUtils.join(branch, page1TextArray[i].trim().split("Branch:")[1]);
                count = 3;
                while (count > 0) {
                    i = i + 1;
                    branch = StringUtils.join(branch, " ", page1TextArray[i].trim());
                    count = count - 1;
                }
            }
        }
        String[] page2TextArray = page2TextIcici.split("\n");
        for (String data : page2TextArray) {
            if (data.contains("Savings")) {
                String[] dataArray = data.split(" ");
                accountNumber = dataArray[1];
                micr = dataArray[2];
                ifsc = dataArray[3];
            }
        }
        branch = branch.split("Visit")[0];
        BankDetails bankDetails = BankDetails.builder().id(AccountConstants.ICICI).name(name).branch(branch).address(address).ifscCode(ifsc).panNumber(environment.getProperty("pan"))
                .accountNumber(accountNumber).customerId(custId).micrCode(micr).build();
        bankDetailsRepositiory.save(bankDetails);
        
        return "ICICI bank details saved successfully";
    }

    public String saveBankDetailsofSBI() {
        
       
            try {
                List<String> bankDetailsTableList = new BankDetails().bankDetailsTableList();
                page4TableDataSbi = page4TableDataSbi.subList(0, bankDetailsTableList.size() - 1);
                String[] accountDetails = page4TextSortedSbi.split("\n");
                
                Map<String, String> bankDetailsMapperTable = new HashMap<>();
                for (int i = 0; i < bankDetailsTableList.size() - 1; i++) {
                    for (List<String> strings : page4TableDataSbi) {
                        if (strings.get(0).toLowerCase().contains(bankDetailsTableList.get(i).toLowerCase())) {
                            bankDetailsMapperTable.put(bankDetailsTableList.get(i), strings.get(1));
                            break;
                            
                        } else if (!StringUtils.isBlank(page4TextSortedSbi)) {
                            for (int s = 0; s < accountDetails.length; s++) {
                                if (accountDetails[s].contains("Customer ID") && bankDetailsTableList.get(i).equalsIgnoreCase("Customer ID")) {
                                    bankDetailsMapperTable.put(bankDetailsTableList.get(i), accountDetails[s].split(":")[1].trim());
                                    break;
                                }
                                if (accountDetails[s].contains("SAVING ACCOUNT") && bankDetailsTableList.get(i).equalsIgnoreCase("Account Number")) {
                                    bankDetailsMapperTable.put(bankDetailsTableList.get(i), accountDetails[s + 1]);
                                    break;
                                }
                                
                            }
                            
                        }
                        if (!bankDetailsMapperTable.containsKey(bankDetailsTableList.get(i))) {
                            
                            bankDetailsMapperTable.put(bankDetailsTableList.get(i), null);
                        }
                        
                    }
                }
                
                String password = "SBI.password";
                BankDetails bankDetails = BankDetails.builder().id(AccountConstants.SBI).accountNumber(bankDetailsMapperTable.get("Account Number"))
                        .address(bankDetailsMapperTable.get("address")).micrCode(bankDetailsMapperTable.get("micr"))
                        .name(bankDetailsMapperTable.get("name")).ifscCode(bankDetailsMapperTable.get("ifsc")).customerId(bankDetailsMapperTable.get("Customer ID"))
                        .branch(bankDetailsMapperTable.get("branch name")).password(environment.getProperty(password)).panNumber(environment.getProperty("pan"))
                        .build();
                
                bankDetailsRepositiory.save(bankDetails);
                return "SBI bank details saved successfully";
            
            } catch (Exception e) {
                return "Error occurred while saving SBI bank details";
            }
        

    }
    
    private void sortDbData(){
        List<FDs> fds =  fdRepository.findAll();
        // Assuming fds is a list of objects with a getId() method that returns a String
        fds.sort((obj1, obj2) -> {
            int id1 = Integer.parseInt(obj1.getId());
            int id2 = Integer.parseInt(obj2.getId());
            return Integer.compare(id1, id2);
        });
        
        fdRepository.deleteAll();
        fdRepository.saveAll(fds);
    }
    
    

    public String collectFdDataofSBI()  {

        FDs fdObject = new FDs();
        boolean newfds = false;
        Map<String, Integer> fdMapper = fdObject.fdMapper(page3TableDataSbi.get(0));
        Integer id;
        List<FDs> exisingfds = fdRepository.findAll();
         if (!exisingfds.isEmpty()) {
            for (int i = 0; i < exisingfds.size(); i++) {
                if (exisingfds.get(i).getBankName().equals(AccountConstants.SBI)) {
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                    LocalDate date = LocalDate.parse(exisingfds.get(i).getMaturityDate(), formatter);
                    if (date.isBefore(LocalDate.now())) {
                        fdRepository.delete(exisingfds.get(i));
                        exisingfds.remove(i);
                    }
                }
            }
            
           
        }
        exisingfds.sort(Comparator.comparing(FDs::getId));
        int count =1 ;
	    for (int i = 1; i < page3TableDataSbi.size(); i++) {
            boolean flag = false;
	        for (FDs exisingfd : exisingfds) {
                 if (count ==  Integer.parseInt(exisingfd.getId())){
                    count = count+1;
                }
		        if (exisingfd.getNumber().equals(page3TableDataSbi.get(i).get(fdMapper.get("number")))) {
			        flag = true;
		        }
	        }
            if (flag) {
                continue;
            }
            id = count;
	        FDs fd = FDs.builder().id(id.toString()).bankName(AccountConstants.SBI).type(page3TableDataSbi.get(i).get(fdMapper.get(FdEnums.type.toString())))
                    .number(page3TableDataSbi.get(i).get(fdMapper.get(FdEnums.number.toString()))).accountOpenDate(page3TableDataSbi.get(i).get(fdMapper.get(FdEnums.accountOpenDate.toString())))
                    .maturityDate(page3TableDataSbi.get(i).get(fdMapper.get(FdEnums.maturityDate.toString()))).amount(page3TableDataSbi.get(i).get(fdMapper.get(FdEnums.amount.toString())))
                    .Roi(page3TableDataSbi.get(i).get(fdMapper.get(FdEnums.Roi.toString()))).build();

            fdRepository.save(fd);
            newfds = true;
           
        }
        sortDbData();
        if (newfds) {
            return "SBI FD's saved successfully";
        } else {
            return "No new SBI FD to save";
        }
    }
    

    public String collectFdDataofUBI()  {
        boolean newfds = false;
        Integer id;
        List<FDs> exisingfds = fdRepository.findAll();
        if (exisingfds.isEmpty()) {
            id = 0;
        } else {
            for (int i = 0; i < exisingfds.size(); i++) {
                
                if (exisingfds.get(i).getBankName().equals(AccountConstants.UBI)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate date = LocalDate.parse(exisingfds.get(i).getMaturityDate(), formatter);
                    if (date.isBefore(LocalDate.now())) {
                        fdRepository.delete(exisingfds.get(i));
                        exisingfds.remove(i);
                    }
                }
            }
        }
        exisingfds.sort(Comparator.comparing(FDs::getId));
        int count = 1;
        
        String[] textLastPageArray = textLastPageUbi.split("\n");
        int j = 0;
	    
	    for (String s : textLastPageArray) {
            j = j + 1;
            if (s.trim().equalsIgnoreCase("LINKED DEPOSITS")) {
                break;
            }
        }
        j = j + 1;
        while (true) {
            String[] data = textLastPageArray[j++].split(" ");
            if (data.length != 8) {
                break;
            }
            boolean flag = false;
            for (int fd = 0; fd < exisingfds.size(); fd++) {
                
                FDs exisingfd = exisingfds.get(fd);
                if (count ==  Integer.parseInt(exisingfd.getId())){
                    count = count+1;
                    
                }
                if (exisingfd.getNumber().equals(data[2])) {
                    
                    flag = true;
                }
            }
            if (flag) {
                continue;
            }
            id =count;
            FDs fd = FDs.builder().id(id.toString()).bankName(AccountConstants.UBI).type(data[1])
                    .number(data[2]).accountOpenDate(data[3])
                    .maturityDate(data[4]).amount(data[6])
                    .Roi(data[5]).build();

            fdRepository.save(fd);
            count = count + 1;

            newfds = true;
            
        }
    sortDbData();
        if (newfds) {
            return "UBI FD's saved successfully";
        } else {
            return "No new UBI FD to save";
        }
    }
    

    public String expenditureDataofSBI(String monthSelected) {
        if (!bankAccountExpenditureRepositiory.existByIdAndMonth(AccountConstants.SBI,monthSelected).isEmpty()) {
            return "SBI expenditure details already Saved";
        }
        String newText = page4TextSbi.split("TRANSACTION OVERVIEW")[1];
        String[] texts = newText.split("\n");
        SalaryUsage salaryUsage = salaryUsageRepository.findById(monthSelected).orElseThrow();
        Double openingBalance = Double.parseDouble(texts[1].split(":")[1]) +salaryUsage.getSecurity();
        Double closingBalance = Double.parseDouble(texts[2].split(":")[1]);
        AccountExpenditure bankAccountExpenditure = AccountExpenditure.builder().id(AccountConstants.SBI).month(monthSelected)
                .closingBalance(closingBalance).openingBalance(openingBalance).expenditureFromAccount(openingBalance-closingBalance).build();
        bankAccountExpenditureRepositiory.save(bankAccountExpenditure);
        return "Expenditure data of SBI saved successfully";
    }
    

    public String expenditureDataofUBI(String monthSelected) {
        if (!bankAccountExpenditureRepositiory.existByIdAndMonth(AccountConstants.UBI,monthSelected).isEmpty()) {
            return "UBI expenditure details already Saved";
        }
        String openingBalance = "";
        String closingBalance = "";
        String[] texts = textLastPageUbi.split("\n");
        for (int i = 0; i < texts.length; i++) {

            if (texts[i].contains("Opening Balance")) {
                openingBalance = texts[i].split(" ")[7].trim();
            }
            if (texts[i].contains("Closing Balance")) {
                closingBalance = texts[i].split(" ")[7].trim();
            }
        }
        
        SalaryUsage salaryUsage = salaryUsageRepository.findById(monthSelected).orElseThrow();
        Double openingBalancewithAddedMoney = Double.parseDouble(openingBalance.replace(",","")) + salaryUsage.getSelf();
        Double closingBalanceDouble = Double.parseDouble(closingBalance.replace(",","")) ;
        AccountExpenditure bankAccountExpenditure = AccountExpenditure.builder().id(AccountConstants.UBI).month(monthSelected)
                .closingBalance(closingBalanceDouble).openingBalance(openingBalancewithAddedMoney).expenditureFromAccount((openingBalancewithAddedMoney-closingBalanceDouble)).build();
        bankAccountExpenditureRepositiory.save(bankAccountExpenditure);
        return "Expenditure data of UBI saved successfully";
    }
    
    
    public String expenditureDataofIcici(String monthSelected) {
        if (!bankAccountExpenditureRepositiory.existByIdAndMonth(AccountConstants.ICICI,monthSelected).isEmpty()) {
            return "ICICI expenditure details already Saved";
        }
       if (ObjectUtils.isEmpty(tabularIciciStatementdata)){
           return ("Tabulated ICICI statement pdf Required");
       }
       int val =  page1TextIcici.indexOf("B/F");
        String openingAmountString = page1TextIcici.substring(val+4,val+13);
       Double openingAmount = Double.parseDouble(openingAmountString.trim().replace(",",""));
       Double salaryUsed =totalSalaryUsed(getSalaryMap());
        Double closingAmount = openingAmount - salaryUsed;
        AccountExpenditure bankAccountExpenditure = AccountExpenditure.builder().id(AccountConstants.ICICI).month(monthSelected)
                .closingBalance(closingAmount).openingBalance(openingAmount).expenditureFromAccount(salaryUsed).build();
        bankAccountExpenditureRepositiory.save(bankAccountExpenditure);
        return "Expenditure data of ICICI saved successfully";
    }
    
    public String salaryUsage(String month) {
        List<String> expenditureFields = new ArrayList<>(List.of("Self" , "Dad","Mom","Brother","Fd","Mf","Stocks","security","Others"));
        Map<String, Integer> expenditureFieldsValues =getSalaryMap();
            SalaryUsage salaryUsage = SalaryUsage.builder().id(month).self(expenditureFieldsValues.get(expenditureFields.get(0)))
                    .dad(expenditureFieldsValues.get(expenditureFields.get(1))).mom(expenditureFieldsValues.get(expenditureFields.get(2))).brother(expenditureFieldsValues.get(expenditureFields.get(3)))
                    .fixedDeposits(expenditureFieldsValues.get(expenditureFields.get(4))).mutualFunds(expenditureFieldsValues.get(expenditureFields.get(5))).stocks(expenditureFieldsValues.get(expenditureFields.get(6)))
                    .security(expenditureFieldsValues.get(expenditureFields.get(7))).others(expenditureFieldsValues.get(expenditureFields.get(8))).build();
            salaryUsageRepository.save(salaryUsage);
            
	    return "Salary Usage saved successfully for month "+month;
     
	   
    }
    private Map<String, Integer> getSalaryMap(){
        List<String> expenditureFields = new ArrayList<>(List.of("Self" , "Dad","Mom","Brother","Fd","Mf","Stocks","security","Others"));
        Map<String, Integer> expenditureFieldsValues = expenditureFields.stream().collect(Collectors.toMap(key -> key,value -> 0));
        tabularIciciStatementdata.remove(0);
        tabularIciciStatementdata.forEach(data -> {
                if (data.get(3).contains("DR") && data.get(1).toLowerCase().contains("mf")) {
                    int updatedValue = expenditureFieldsValues.get(expenditureFields.get(5)) + Integer.parseInt(data.get(2).split("\\.")[0]);
                    expenditureFieldsValues.put(expenditureFields.get(5),updatedValue);
                }
                
                else if (data.get(3).contains("DR") && (data.get(1).toLowerCase().contains("father") || data.get(1).toLowerCase().contains("papa") || data.get(1).toLowerCase().contains("9425330440"))){
                    int updatedValue = expenditureFieldsValues.get(expenditureFields.get(1)) + Integer.parseInt(data.get(2).split("\\.")[0]);
                    expenditureFieldsValues.put(expenditureFields.get(1),updatedValue);
                }
                else if (data.get(3).contains("DR") &&(data.get(1).toLowerCase().contains("mother") || data.get(1).toLowerCase().contains("mom") || data.get(1).toLowerCase().contains("mummy"))){
                    int updatedValue = expenditureFieldsValues.get(expenditureFields.get(2)) + Integer.parseInt(data.get(2).split("\\.")[0]);
                    expenditureFieldsValues.put(expenditureFields.get(2),updatedValue);
                }
                else if (data.get(3).contains("DR") &&(data.get(1).toLowerCase().contains("brother") || data.get(1).toLowerCase().contains("sushant") || data.get(1).toLowerCase().contains("9424599340"))){
                    int updatedValue = expenditureFieldsValues.get(expenditureFields.get(3)) + Integer.parseInt(data.get(2).split("\\.")[0]);
                    expenditureFieldsValues.put(expenditureFields.get(3),updatedValue);
                }
                else if ((data.get(1).toLowerCase().contains("9424333440@ibl") && data.get(1).toLowerCase().contains("union bank of i")) || data.get(1).toLowerCase().contains("cred") ){
                    int updatedValue = expenditureFieldsValues.get(expenditureFields.get(0));
                    
                    if (data.get(3).contains("DR")) {
                         updatedValue = expenditureFieldsValues.get(expenditureFields.get(0)) + Integer.parseInt(data.get(2).split("\\.")[0]);
                    }else if(data.get(3).contains("CR")) {
                        
                         updatedValue = expenditureFieldsValues.get(expenditureFields.get(0)) - Integer.parseInt(data.get(2).split("\\.")[0]);
                    }
                    expenditureFieldsValues.put(expenditureFields.get(0),updatedValue);
                }
                else if ((
                        data.get(1).toLowerCase().contains("security money") || data.get(1).toLowerCase().contains("self") || data.get(1).toLowerCase().contains("bank account xx")) && data.get(1).toLowerCase().contains("state bank of i")) {
                    int updatedValue = expenditureFieldsValues.get(expenditureFields.get(7));
                    if (data.get(3).contains("DR")) {
                     updatedValue = expenditureFieldsValues.get(expenditureFields.get(7)) + Integer.parseInt(data.get(2).split("\\.")[0]);
                    }else if(data.get(3).contains("CR")) {
                        updatedValue = expenditureFieldsValues.get(expenditureFields.get(7)) - Integer.parseInt(data.get(2).split("\\.")[0]);
                    }
                        expenditureFieldsValues.put(expenditureFields.get(7), updatedValue);
                    
                }
                else if (data.get(3).contains("DR") &&(data.get(1).toLowerCase().contains("zerodha") && data.get(1).toLowerCase().contains("broking") )){
                    int updatedValue = expenditureFieldsValues.get(expenditureFields.get(6)) + Integer.parseInt(data.get(2).split("\\.")[0]);
                    expenditureFieldsValues.put(expenditureFields.get(6),updatedValue);
                }
                else if(data.get(3).contains("DR")){
                    int updatedValue = expenditureFieldsValues.get(expenditureFields.get(8)) + Integer.parseInt(data.get(2).split("\\.")[0]);
                    expenditureFieldsValues.put(expenditureFields.get(8),updatedValue);
                }
            });
            expenditureFieldsValues.put(expenditureFields.get(4), fdRepository.findAll().stream().filter(fd -> LocalDate.now().minusMonths(1).getMonthValue() ==  Integer.parseInt(fd.getAccountOpenDate().split("-")[1])).map(fd -> (int)Double.parseDouble(fd.getAmount().replace(",",""))).reduce(0,Integer::sum));
            return expenditureFieldsValues;
        
        }
        private Double totalSalaryUsed(Map<String, Integer> expenditureFieldsValues){
            Double totalAmount= 0.0;
            for ( Map.Entry<String, Integer> data : expenditureFieldsValues.entrySet()){
            totalAmount += data.getValue().doubleValue();
            }
            return totalAmount;
        }
        
        
        public String yearlyExpenditure(){
            List<YearlyExpenditure> yearlyExpendituresaved = yearlyExpenditureRepository.findAll();
            List<String> savedMonths =yearlyExpendituresaved.stream().map(YearlyExpenditure::getId).toList();
            List<String> monthNames = Arrays.stream(Month.values()).map(Month::name).collect(Collectors.toList());
            for (int i = 0; i < 3; i++) {
                String month = monthNames.remove(0);
                monthNames.add(month);
            }
            
            
            for (int i = 1; i < monthNames.size(); i++) {
                String monthTemp = monthNames.get(i);
                if (!savedMonths.contains(monthTemp) && LocalDate.now().getMonth().getValue() - (i + 3) > 0) {
                    Optional<AccountExpenditure> iciciOptional = bankAccountExpenditureRepositiory.findExpenditureFromAccount(AccountConstants.ICICI, month);
                    Double icici = 0.0;
                    Double sbi = 0.0;
                    Double ubi = 0.0;
                    if (iciciOptional.isPresent()) {
                        AccountExpenditure iciciAccountExpenditure = iciciOptional.get();
                        icici = ObjectUtils.isNotEmpty(iciciAccountExpenditure.getExpenditureFromAccount()) ? iciciAccountExpenditure.getExpenditureFromAccount() : 0.0;
                        
                    }
                    Optional<AccountExpenditure> sbiOptional = bankAccountExpenditureRepositiory.findExpenditureFromAccount(AccountConstants.SBI, month);
                    if (sbiOptional.isPresent()) {
                        AccountExpenditure iciciAccountExpenditure = sbiOptional.get();
                        sbi = ObjectUtils.isNotEmpty(iciciAccountExpenditure.getExpenditureFromAccount()) ? iciciAccountExpenditure.getExpenditureFromAccount() : 0.0;
                        
                    }
                    
                    Optional<AccountExpenditure> ubiOptional = bankAccountExpenditureRepositiory.findExpenditureFromAccount(AccountConstants.UBI, month);
                    if (ubiOptional.isPresent()) {
                        AccountExpenditure iciciAccountExpenditure = ubiOptional.get();
                        ubi = ObjectUtils.isNotEmpty(iciciAccountExpenditure.getExpenditureFromAccount()) ? iciciAccountExpenditure.getExpenditureFromAccount() : 0.0;
                        
                    }
                    YearlyExpenditure yearlyExpenditure = new YearlyExpenditure(month, sbi, ubi, icici, sbi + ubi + icici);
                    yearlyExpenditureRepository.save(yearlyExpenditure);
                }
            };
           return "All expenditure saved for year till now.";
        }
    

}
