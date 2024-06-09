package com.yogi.account.Account.service;

import com.yogi.account.Account.dto.BulkUpdateFileUploadMetaDataRequest;
import com.yogi.account.Account.entity.UploadMetaData;

import java.util.List;

public interface FileUploadMetadataService {
	
	UploadMetaData fileUploadMetaData(String type);
	
	UploadMetaData monthDependentUploadMetaData(String type, int expenditureButton, List<String> attachmentTitleKey);
	
	String saveuploadMetaData(UploadMetaData data);
	
	UploadMetaData bulkUpdateInvestmentAccountFileUploadMetaData(BulkUpdateFileUploadMetaDataRequest categoryList);
	
	UploadMetaData bulkUpdateBankAccountFileUploadMetaData(BulkUpdateFileUploadMetaDataRequest categoryList);
}
