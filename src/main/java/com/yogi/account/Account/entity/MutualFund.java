package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;


@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@Document(collection = "MutualFund")
public class MutualFund {
	
	
	@MongoId
	 String id;
	 String companyName;
	 String mutualFundName;
	 String fundType;
	 double currentBalance;
	 double freeBalance;
	 double rate;
	 double amount;
}
