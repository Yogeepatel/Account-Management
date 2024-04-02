package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.entity.ELSS;
import com.yogi.account.Account.pdfManipulation.PdfBox;
import com.yogi.account.Account.pdfManipulation.Tabula;
import com.yogi.account.Account.repository.ELSSRepository;
import com.yogi.account.Account.service.FetchMfStocksService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class FetchMfStocksServiceImpl implements FetchMfStocksService {
	private Tabula tabula;
	private PdfBox pdfBox;
	@Autowired
	private ELSSRepository elssRepository;
	
	@Override
	public String addElssStatementData() {
		
		try {
			int count=0;
			List<ELSS> retrievedData = elssRepository.findAll();
			List<String> folioNos = retrievedData.stream().map(obj -> obj.getFolio_number()).toList();
			if (!ObjectUtils.isEmpty(retrievedData)){
				count = retrievedData.size();
			}
			
				PDDocument document = Loader.loadPDF(ResourceUtils.getFile("classpath:UplodedFiles/ELSS.pdf"));
				
				List<List<String>> result = tabula.pdfPartialTableReader(document, 1);
				
				for (int i = 1; i < result.size(); i++) {
					if (!folioNos.contains(result.get(i).get(1))) {
						ELSS elss = new ELSS().builder().id(Integer.toString(i + count)).Amount(result.get(i).get(4))
								.Units(Double.parseDouble(result.get(i).get(3))).Folio_number(result.get(i).get(1))
								.Name_of_fund(result.get(i).get(2)).Purchase_Date(result.get(i).get(0)).build();
						elssRepository.save(elss);
					}
				}
				return "Data Saved Successfully";
			
		} catch (Exception e) {
			return "Error Occuered while reading pdf " + e.getMessage();
		}
		
	}

}
