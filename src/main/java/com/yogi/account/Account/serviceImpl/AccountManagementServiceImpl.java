package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.entity.ELSS;
import com.yogi.account.Account.pdfManipulation.PdfWriter;
import com.yogi.account.Account.pdfManipulation.Tabula;
import com.yogi.account.Account.repository.ELSSRepository;
import com.yogi.account.Account.service.AccountManagementService;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountManagementServiceImpl implements AccountManagementService {

    public final PdfWriter pdfWriter = new PdfWriter();
    public final Tabula tabula = new Tabula();

    private ELSSRepository elssRepository;

    @Override
    public String addElssStatementData() {

        try {

            PDDocument document = Loader.loadPDF(ResourceUtils.getFile("classpath:UplodedFiles/ELSS.pdf"));

            List<List<String>> result = tabula.pdfPartialTableReader(document, 1);

            for (int i = 1; i < result.size(); i++) {
                ELSS elss = new ELSS();
                elss.setAmount(result.get(i).get(4));
                elss.setUnits(Double.parseDouble(result.get(i).get(3)));
                elss.setFolio_number(result.get(i).get(2));
                elss.setName_of_fund(result.get(i).get(1));
                elss.setPurchase_Date(result.get(i).get(0));
                elssRepository.save(elss);
            }
            return "Data Saved Successfully";

        } catch (Exception e) {
            return "ELSS File not uploaded";
        }

    }


}
