package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.constants.AccountConstants;
import com.yogi.account.Account.constants.FdEnums;
import com.yogi.account.Account.entity.AccountExpenditure;
import com.yogi.account.Account.entity.BankDetails;
import com.yogi.account.Account.entity.ELSS;
import com.yogi.account.Account.entity.FDs;
import com.yogi.account.Account.pdfManipulation.PdfBox;
import com.yogi.account.Account.pdfManipulation.PdfWriter;
import com.yogi.account.Account.pdfManipulation.Tabula;
import com.yogi.account.Account.repository.AccountExpenditureRepository;
import com.yogi.account.Account.repository.BankDetailsRepositiory;
import com.yogi.account.Account.repository.ELSSRepository;
import com.yogi.account.Account.repository.FDRepository;
import com.yogi.account.Account.service.AccountManagementService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AccountManagementServiceImpl implements AccountManagementService {

    public PdfWriter pdfWriter;
    public Tabula tabula;
    public PdfBox pdfBox;

    @Autowired
    private Environment environment;

    @Autowired
    private ELSSRepository elssRepository;
    @Autowired
    private BankDetailsRepositiory bankDetailsRepositiory;
    @Autowired
    private FDRepository fdRepository;
    @Autowired
    private AccountExpenditureRepository bankAccountExpenditureRepositiory;


    @Override
    public String addElssStatementData() {

        try {

            PDDocument document = Loader.loadPDF(ResourceUtils.getFile("classpath:UplodedFiles/ELSS.pdf"));

            List<List<String>> result = tabula.pdfPartialTableReader(document, 1);

            for (int i = 1; i < result.size(); i++) {
                ELSS elss = new ELSS().builder().Amount(result.get(i).get(4))
                        .Units(Double.parseDouble(result.get(i).get(3))).Folio_number(result.get(i).get(2))
                        .Name_of_fund(result.get(i).get(1)).Purchase_Date(result.get(i).get(0)).build();
                elssRepository.save(elss);
            }
            return "Data Saved Successfully";

        } catch (Exception e) {
            return "Error Occuered while reading pdf " + e.getMessage();
        }

    }

    @Override
    public String fetchSbiStatementData(String task) throws Exception {
        String page4Text = "";
        List<List<String>> tabledatapage4 = null;
        List<List<String>> tabledatapage3 = null;
        PDDocument document = null;
        String password = "SBI.password";
        try {
            document = Loader.loadPDF(ResourceUtils.getFile("classpath:UplodedFiles/SBI.pdf"), environment.getProperty(password));
            tabledatapage4 = tabula.pdfTableReader(document, document.getNumberOfPages());
            tabledatapage3 = tabula.pdfTableReaderWithRuling(document, document.getNumberOfPages() - 1);
            document = Loader.loadPDF(ResourceUtils.getFile("classpath:UplodedFiles/SBI.pdf"), environment.getProperty(password));
            page4Text = pdfBox.read(document, document.getNumberOfPages(), false);
        } catch (Exception e) {
            return ("Error Occuered while reading pdf " + e.getMessage());
        }
        if (document == null || CollectionUtils.isEmpty(tabledatapage4) || StringUtils.isBlank(page4Text) || CollectionUtils.isEmpty(tabledatapage3)) {
            return "Error Occuered while reading pdf ";
        }

        if (task.equals("Save Bank Details")) {
            return saveBankDetailsofSBI(page4Text, tabledatapage4);
        }
        String expenditureResponse = ExpenditureDataofSBI(page4Text);
        String fdResponse = collectFdDataofSBI(tabledatapage3);
        return expenditureResponse + "\n " + fdResponse;
    }

    @Override
    public String fetchUbiStatementData(String task) throws Exception {
        List<List<String>> tabledatapage1 = null;
        List<List<String>> tabledatapagelast = null;
        String textLastPage = null;
        PDDocument document = null;
        String password = "UBI.password";
        try {
            document = Loader.loadPDF(ResourceUtils.getFile("classpath:UplodedFiles/UBI.pdf"), environment.getProperty(password));
            tabledatapage1 = tabula.pdfTableReader(document, 1);
            tabledatapagelast = tabula.pdfTableReader(document, document.getNumberOfPages() - 1);

            textLastPage = pdfBox.read(document, document.getNumberOfPages() - 1, true);
        } catch (Exception e) {
            return ("Error Occuered while reading pdf " + e.getMessage());
        }
        if (document == null || CollectionUtils.isEmpty(tabledatapage1) || StringUtils.isBlank(textLastPage) || CollectionUtils.isEmpty(tabledatapagelast)) {
            return "Error Occuered while reading pdf ";
        }

        if (task.equals("Save Bank Details")) {
            return saveBankDetailsofUBI(tabledatapage1);
        }
        String expenditureResponse = ExpenditureDataofUBI(tabledatapagelast, textLastPage);
        String fdResponse = collectFdDataofUBI(tabledatapagelast, textLastPage);
        return expenditureResponse + "\n " + fdResponse;
    }


    private String saveBankDetailsofUBI(List<List<String>> result) {
        if (bankDetailsRepositiory.existsById(AccountConstants.UBI)) {
            return "Details already Saved";
        } else {
            try {
                List<String> bankDetailsTableList = new BankDetails().bankDetailsTableList();
                result = result.subList(0, bankDetailsTableList.size() - 1);
                boolean check = false;

                Map<String, String> bankDetailsMapperTable = new HashMap<>();
                for (int i = 0; i < bankDetailsTableList.size() - 1; i++) {
                    for (int j = 0; j < result.size(); j++) {
                        for (int k = 0; k < result.get(j).size(); k++) {
                            if (result.get(j).get(k).toLowerCase().contains(bankDetailsTableList.get(i).toLowerCase())) {
                                String[] spliyararay = result.get(j).get(k).split("&");
                                if (spliyararay.length > 1) {
                                    if (spliyararay[0].toLowerCase().trim().equalsIgnoreCase(bankDetailsTableList.get(i).toLowerCase())) {
                                        bankDetailsMapperTable.put(bankDetailsTableList.get(i), result.get(j + 1).get(0));
                                    }
                                    if (spliyararay[1].trim().toLowerCase().contains(bankDetailsTableList.get(i).toLowerCase())) {
                                        String address = "";
                                        for (int s = 2; s < result.size(); s++) {
                                            address = StringUtils.join(address, result.get(s).get(0), " ");
                                        }
                                        bankDetailsMapperTable.put(bankDetailsTableList.get(i), address);
                                    }
                                } else {
                                    bankDetailsMapperTable.put(bankDetailsTableList.get(i), result.get(j).get(k + 1));
                                }
                            }
                        }
                    }
                }
                String password = "SBI.password";
                BankDetails bankDetails = new BankDetails().builder().id(AccountConstants.SBI).accountNumber(bankDetailsMapperTable.get("Account Number"))
                        .address(bankDetailsMapperTable.get("address")).micrCode(bankDetailsMapperTable.get("micr"))
                        .name(bankDetailsMapperTable.get("name")).ifscCode(bankDetailsMapperTable.get("ifsc")).customerId(bankDetailsMapperTable.get("Customer ID"))
                        .branch(bankDetailsMapperTable.get("branch name")).password(environment.getProperty(password))
                        .build();
                String id = bankDetailsRepositiory.save(bankDetails).getId();
                return "Data Saved Successfully";
            } catch (Exception e) {
                e.printStackTrace();
                return "Error Occured while saving data";
            }
        }
    }

    private String saveBankDetailsofSBI(String text, List<List<String>> result) {
        if (bankDetailsRepositiory.existsById(AccountConstants.SBI)) {
            return "Details already Saved";
        } else {
            try {
                List<String> bankDetailsTableList = new BankDetails().bankDetailsTableList();
                result = result.subList(0, bankDetailsTableList.size() - 1);
                String[] accountDetails = text.split("\n");

                Map<String, String> bankDetailsMapperTable = new HashMap<>();
                for (int i = 0; i < bankDetailsTableList.size() - 1; i++) {
                    for (int j = 0; j < result.size(); j++) {
                        if (result.get(j).get(0).toLowerCase().contains(bankDetailsTableList.get(i).toLowerCase())) {
                            bankDetailsMapperTable.put(bankDetailsTableList.get(i), result.get(j).get(1));
                            break;

                        } else if (!StringUtils.isBlank(text)) {
                            for (int s = 0; s < accountDetails.length; s++) {
                                if (accountDetails[s].contains("Customer ID") && bankDetailsTableList.get(i).toLowerCase().equals("Customer ID".toLowerCase())) {
                                    bankDetailsMapperTable.put(bankDetailsTableList.get(i), accountDetails[s].split(":")[1].trim());
                                    break;
                                }
                                if (accountDetails[s].contains("SAVING ACCOUNT") && bankDetailsTableList.get(i).toLowerCase().equals("Account Number".toLowerCase())) {
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
                BankDetails bankDetails = new BankDetails().builder().id(AccountConstants.SBI).accountNumber(bankDetailsMapperTable.get("Account Number"))
                        .address(bankDetailsMapperTable.get("address")).micrCode(bankDetailsMapperTable.get("micr"))
                        .name(bankDetailsMapperTable.get("name")).ifscCode(bankDetailsMapperTable.get("ifsc")).customerId(bankDetailsMapperTable.get("Customer ID"))
                        .branch(bankDetailsMapperTable.get("branch name")).password(environment.getProperty(password))
                        .build();
                String id = bankDetailsRepositiory.save(bankDetails).getId();
                return "Data Saved Successfully";
            } catch (Exception e) {
                e.printStackTrace();
                return "Error Occured while saving data";
            }
        }

    }

    private String collectFdDataofSBI(List<List<String>> text) throws Exception {

        FDs fdObjecct = new FDs();
        boolean newfds = false;
        List<String> fdFormat = fdObjecct.fdTableList();
        Map<String, Integer> fdMapper = fdObjecct.fdMapper(text.get(0));
        Integer id;
        List<FDs> exisingfds = fdRepository.findAll();
        if (exisingfds.isEmpty()) {
            id = 0;
        } else {
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
            id = exisingfds.size();
        }


        for (int i = 1; i < text.size(); i++) {
            boolean flag = false;
            for (int fd = 0; fd < exisingfds.size(); fd++) {
                if (exisingfds.get(fd).getNumber().equals(text.get(i).get(fdMapper.get("number")))) {
                    flag = true;
                }
            }
            if (flag) {
                continue;
            }
            id = id + 1;
            FDs fd = new FDs().builder().id(id.toString()).bankName(AccountConstants.SBI).type(text.get(i).get(fdMapper.get(FdEnums.type.toString())))
                    .number(text.get(i).get(fdMapper.get(FdEnums.number.toString()))).accountOpenDate(text.get(i).get(fdMapper.get(FdEnums.accountOpenDate.toString())))
                    .maturityDate(text.get(i).get(fdMapper.get(FdEnums.maturityDate.toString()))).amount(text.get(i).get(fdMapper.get(FdEnums.amount.toString())))
                    .Roi(text.get(i).get(fdMapper.get(FdEnums.Roi.toString()))).build();

            fdRepository.save(fd);
            newfds = true;

        }
        if (newfds) {
            return "FD Saved Successfully";
        } else {
            return "No New FD ";
        }
    }

    private String collectFdDataofUBI(List<List<String>> text, String textLastPage) throws Exception {
        FDs fdObjecct = new FDs();
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
            id = exisingfds.size();
        }
        String[] textLastPageArray = textLastPage.split("\n");
        int j = 0;

        String[] textSubArray = new String[0];
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
                if (exisingfds.get(fd).getNumber().equals(data[2])) {
                    flag = true;
                }
            }
            if (flag) {
                continue;
            }
            id = id + 1;
            FDs fd = new FDs().builder().id(id.toString()).bankName(AccountConstants.UBI).type(data[1])
                    .number(data[2]).accountOpenDate(data[3])
                    .maturityDate(data[4]).amount(data[6])
                    .Roi(data[5]).build();

            fdRepository.save(fd);
            newfds = true;
        }

        if (newfds) {
            return "FD Saved Successfully";
        } else {
            return "No New FD ";
        }
    }

    private String ExpenditureDataofSBI(String text) {
        if (bankAccountExpenditureRepositiory.existsById(AccountConstants.SBI)) {
            return "Details already Saved";
        }
        String newText = text.split("TRANSACTION OVERVIEW")[1];
        String[] texts = newText.split("\n");
        AccountExpenditure bankAccountExpenditure = new AccountExpenditure().builder().id(AccountConstants.SBI)
                .closingBalance(texts[2]).openingBalance(texts[1]).build();
        bankAccountExpenditureRepositiory.save(bankAccountExpenditure);
        return "Expenditure Data Saved Successfully";
    }

    private String ExpenditureDataofUBI(List<List<String>> table, String text) {
        if (bankAccountExpenditureRepositiory.existsById(AccountConstants.UBI)) {
            return "Details already Saved";
        }
        String openingBalance = "";
        String closingBalance = "";
        String[] texts = text.split("\n");
        for (int i = 0; i < texts.length; i++) {

            if (texts[i].contains("Opening Balance")) {
                openingBalance = texts[i].split(" ")[7].trim();
            }
            if (texts[i].contains("Closing Balance")) {
                closingBalance = texts[i].split(" ")[7].trim();
            }
        }
        AccountExpenditure bankAccountExpenditure = new AccountExpenditure().builder().id(AccountConstants.UBI)
                .closingBalance(closingBalance).openingBalance(openingBalance).build();
        bankAccountExpenditureRepositiory.save(bankAccountExpenditure);
        return "Expenditure Data Saved Successfully";
    }

}
