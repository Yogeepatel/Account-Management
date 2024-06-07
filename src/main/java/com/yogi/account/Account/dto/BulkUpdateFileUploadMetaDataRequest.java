package com.yogi.account.Account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkUpdateFileUploadMetaDataRequest {
	@JsonProperty("updateAllBankAccounts")
	boolean bankAccountsUpdateAll;
	
	@JsonProperty("updateAllInvestmentAccounts")
	boolean investmentAccountsUpdateAll;
	
	@JsonProperty("updateRequiredBankAccountCategory")
	List<String> updateRequiredBankAccountCategory;
	
		@JsonProperty("updateRequiredInvestmentAccountCategory")
	List<String> updateRequiredInvestmentAccountCategory;
	
}
