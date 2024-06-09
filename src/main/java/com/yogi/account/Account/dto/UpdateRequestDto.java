package com.yogi.account.Account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestDto {
	Map<String,String> filesNameMap;
	List<String> attachmentTitleKey;
	@JsonProperty("updateRequiredBankAccountCategory")
	List<String> bankAccountCategoriesToUpdate;
	@JsonProperty("updateRequiredInvestmentAccountCategory")
	List<String> investmentAccountcategoriesToUpdate;
	boolean updateAllBankAccounts;
	
	
	boolean updateAllInvestmentAccounts;
}
