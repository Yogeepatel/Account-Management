package com.yogi.account.Account.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;


@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "MutualFund")
public class MutualFund {
	
	
	@MongoId
	private String id;
	private String companyName;
	private String mutualFundName;
	private double currentBalance;
	private double freeBalance;
	private double rate;
	private double amount;
}
