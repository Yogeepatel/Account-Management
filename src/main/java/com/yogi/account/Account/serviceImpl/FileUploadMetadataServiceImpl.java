package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.dto.BulkUpdateFileUploadMetaDataRequest;
import com.yogi.account.Account.entity.UploadMetaData;
import com.yogi.account.Account.repository.FileUploadMetaDataRepository;
import com.yogi.account.Account.service.FileUploadMetadataService;
import com.yogi.account.Account.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FileUploadMetadataServiceImpl implements FileUploadMetadataService {
	private FileUploadMetaDataRepository uploadMetaDataRepository;
	
	public UploadMetaData fileUploadMetaData(String type) {
		Optional<UploadMetaData> optional = uploadMetaDataRepository.findById(type);
		if (optional.isEmpty()) {
			throw new RuntimeException("File format meta data not found.");
		}
		
		return optional.get();
	}
	
	@Override
	public UploadMetaData monthDependentUploadMetaData(String type, int expenditureButton, List<String> attachmentTitleKey) {
		if (type.equalsIgnoreCase("salaryUsage")) {
			return new UploadMetaData("salaryUsage", 2, 2, 0, Map.of("File1", "Upload ICICI Pdf Statement", "File2", "Upload Tabulated ICICI Pdf Statement"));
		} else if (type.equalsIgnoreCase("monthly")) {
			if (expenditureButton == 2) {
				return AccountUtils.accountExpenditureFileUploadMetaData(attachmentTitleKey);
			} else if (expenditureButton == 1) {
				return AccountUtils.monthExpenditureFileUploadMetaData();
			}
		}
		return null;
	}
	
	@Override
	public String saveuploadMetaData(UploadMetaData data) {
		uploadMetaDataRepository.save(data);
		Optional<UploadMetaData> optional = uploadMetaDataRepository.findById(data.getId());
		if (optional.isEmpty()) {
			throw new RuntimeException("Data not found.");
		}
		return "Data Saved successfully";
	}
	
	
	public UploadMetaData bulkUpdateInvestmentAccountFileUploadMetaData(BulkUpdateFileUploadMetaDataRequest categoryList) {
		if (categoryList.isInvestmentAccountsUpdateAll()) {
			UploadMetaData elssOptionalInfo = AccountUtils.elssFileUploadMetaData();
			UploadMetaData stocksOptionalInfo = AccountUtils.investmentAccountFileUploadMetaData();
			Map<String, String> attachmentTitle = new HashMap<>(stocksOptionalInfo.getAttachmentTitle());
			attachmentTitle.put("file3",elssOptionalInfo.getAttachmentTitle().get("file1"));
			return UploadMetaData.builder().id("invesments").attachmentTitle(attachmentTitle).csvRequired(elssOptionalInfo.getCsvRequired() + stocksOptionalInfo.getCsvRequired()).totalFileRequied(elssOptionalInfo.getTotalFileRequied() + stocksOptionalInfo.getTotalFileRequied()).pdfRequired(elssOptionalInfo.getPdfRequired() + stocksOptionalInfo.getPdfRequired()).build();
		} else {
			if (!categoryList.getUpdateRequiredInvestmentAccountCategory().isEmpty()) {
				Map<String, String> attachmentTitle = new HashMap<>();
				int count = 0;
				int csvFiles = 0;
				int pdfFiles = 0;
				for (String key : categoryList.getUpdateRequiredInvestmentAccountCategory()) {
					if (key.equalsIgnoreCase("ELSS")) {
						
						UploadMetaData elssAttachmentTitle = AccountUtils.elssFileUploadMetaData();
						pdfFiles = pdfFiles + elssAttachmentTitle.getPdfRequired();
						csvFiles = csvFiles + elssAttachmentTitle.getCsvRequired();
						for (Map.Entry<String, String> data : elssAttachmentTitle.getAttachmentTitle().entrySet()) {
							count = count + 1;
							attachmentTitle.put("file" + count, data.getValue());
						}
						
					} else {
						
						UploadMetaData stocksAttachmentTitle = AccountUtils.investmentAccountFileUploadMetaData();
						
						for (Map.Entry<String, String> data : stocksAttachmentTitle.getAttachmentTitle().entrySet()) {
							if (!attachmentTitle.values().contains(data.getValue())) {
								count = count + 1;
								attachmentTitle.put("file" + count, data.getValue());
								
								
								if (data.getValue().toLowerCase().contains("csv"))
									csvFiles = csvFiles + 1;
								else
									pdfFiles = pdfFiles + 1;
							}
						}
					}
					
				}
				return UploadMetaData.builder().id("invesments").pdfRequired(pdfFiles).csvRequired(csvFiles).totalFileRequied(pdfFiles + csvFiles).attachmentTitle(attachmentTitle).build();
			}
		}
		return  null;
		}
	
	
	
	public UploadMetaData bulkUpdateBankAccountFileUploadMetaData(BulkUpdateFileUploadMetaDataRequest categoryList) {
		if (categoryList.isBankAccountsUpdateAll()) {
			return AccountUtils.monthExpenditureFileUploadMetaData();
		} else {
			if(!categoryList.getUpdateRequiredBankAccountCategory().isEmpty()) {
				Map<String, String> attachmentTitle = new HashMap<>();
				int csvFiles = 0;
				int pdfFiles = 0;
				if (categoryList.getUpdateRequiredBankAccountCategory().contains("salaryUsage")) {
					UploadMetaData salaryUsageUploadMetadata = AccountUtils.salaryUsageFileUploadMetaData();
					attachmentTitle.putAll(salaryUsageUploadMetadata.getAttachmentTitle());
					csvFiles = salaryUsageUploadMetadata.getCsvRequired();
					pdfFiles = salaryUsageUploadMetadata.getPdfRequired();
					categoryList.getUpdateRequiredBankAccountCategory().remove("salaryUsage");
				}
				if (!categoryList.getUpdateRequiredBankAccountCategory().isEmpty()) {
					UploadMetaData bankAccountFileUploadMetadata = AccountUtils.bankAccounkBulkUpdateFileUploadMetaData(categoryList.getUpdateRequiredBankAccountCategory().stream().map(value -> value.split("\n")[1]).toList()
					);
					int count = attachmentTitle.size();
					for (Map.Entry<String, String> key : bankAccountFileUploadMetadata.getAttachmentTitle().entrySet()) {
						count = count + 1;
						if (!attachmentTitle.values().contains(key.getValue())) {
							attachmentTitle.put("File" + count, key.getValue());
							pdfFiles = pdfFiles + 1;
						}
					}
					
				}
				return UploadMetaData.builder().id("bankAccount").pdfRequired(pdfFiles).csvRequired(csvFiles).totalFileRequied(pdfFiles + csvFiles).attachmentTitle(attachmentTitle).build();
				
			}
			return null;
		}
	}
}
