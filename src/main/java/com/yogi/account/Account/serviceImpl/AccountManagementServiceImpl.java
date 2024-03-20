package com.yogi.account.Account.serviceImpl;

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
import com.yogi.account.Account.utils.AccountUtils;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AccountManagementServiceImpl implements AccountManagementService {

    public final PdfWriter pdfWriter = new PdfWriter();
    public final Tabula tabula = new Tabula();
    public final PdfBox pdfBox = new PdfBox();

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
        String bankName = "SBI";
        String page4Text = "";
        List<List<String>> tabledatapage4 = null;
        List<List<String>> tabledatapage3 = null;
        PDDocument document = null;
        try {
            document = Loader.loadPDF(ResourceUtils.getFile("classpath:UplodedFiles/SBI.pdf"), "33440140700");
            tabledatapage4 = tabula.pdfTableReader(document, document.getNumberOfPages());
            tabledatapage3 = tabula.pdfTableReaderWithRuling(document, 3);
            page4Text = pdfBox.read(AccountUtils.copy(document), 4);
        } catch (Exception e) {
            return ("Error Occuered while reading pdf " + e.getMessage());
        }
        if (document == null || CollectionUtils.isEmpty(tabledatapage4) || StringUtils.isBlank(page4Text) || CollectionUtils.isEmpty(tabledatapage3)) {
            return "Error Occuered while reading pdf ";
        }

        if (task.equals("Save Bank Details")) {
            return saveBankDetails(page4Text, tabledatapage4,bankName);
        }
        String expenditureResponse = ExpenditureData(page4Text, bankName);
        String fdResponse = collectFdData(tabledatapage3, bankName);
        return expenditureResponse + "\n " + fdResponse;
    }


    private String saveBankDetails(String text, List<List<String>> result,String bankName) {
        if (bankDetailsRepositiory.existsById("SBI")) {
            return "Details already Saved";
        } else {
            try {
                String[] texts = text.split("TRANSACTION DETAILS|TRANSACTION OVERVIEW");

                String[] accountDetails = texts[1].split("\n");
                String accountNumber = accountDetails[1];
                BankDetails bankDetails = new BankDetails().builder().id(bankName).accountNumber(accountNumber).address(result.get(1)
                                .get(1)).name(result.get(2).get(1)).password("33440140700").ifscCode(result.get(7).get(1))
                        .branch(result.get(4).get(1)).branchCode(result.get(5).get(1)).build();
                String id = bankDetailsRepositiory.save(bankDetails).getId();
                return "Data Saved Successfully";
            } catch (Exception e) {
                return "Error Occured while saving data";
            }
        }

    }

    private String collectFdData(List<List<String>> text, String bankName) throws Exception {
        FDs fdObjecct = new FDs();
        List<String> fdFormat = fdObjecct.fdTableList();
        Map<String, Integer> fdMapper = fdObjecct.fdMapper(text.get(0));
        Integer id;
        List<FDs> exisingfds = fdRepository.findAll();
        if (exisingfds.isEmpty()) {
            id = 0;
        } else {
            for (int fd = 1; fd < exisingfds.size(); fd++) {

            }
            id = exisingfds.size();
        }


        for (int i = 1; i < text.size(); i++) {
            if (exisingfds.get(i).getAccountNumber().equals(text.get(i).get(fdMapper.get("account Number")))) {
                continue;
            }
            id = id + 1;
            FDs fd = new FDs().builder().id(id.toString()).bankName(bankName).type(text.get(i).get(fdMapper.get(fdFormat.get(2))))
                    .accountNumber(text.get(i).get(fdMapper.get(fdFormat.get(3)))).accountOpenDate(text.get(i).get(fdMapper.get(fdFormat.get(4))))
                    .maturityDate(text.get(i).get(fdMapper.get(fdFormat.get(6)))).amount(text.get(i).get(fdMapper.get(fdFormat.get(7))))
                    .Roi(text.get(i).get(fdMapper.get(fdFormat.get(5)))).build();

            fdRepository.save(fd);

        }
        return "FD Saved Successfully";
    }

    private String ExpenditureData(String text, String bankName) {
        String newText = text.split("TRANSACTION OVERVIEW")[1];
        String[] texts = newText.split("\n");
        AccountExpenditure bankAccountExpenditure = new AccountExpenditure().builder().id("1").bankName(bankName)
                .closingBalance(texts[2]).openingBalance(texts[1]).build();
        bankAccountExpenditureRepositiory.save(bankAccountExpenditure);
        return "Expenditure Data Saved Successfully";
    }
}
