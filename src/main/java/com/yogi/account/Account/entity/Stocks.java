package com.yogi.account.Account.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "STOCKS")
public class Stocks {
	
	@MongoId
	private String id;
	private String stockName;
	private String stockCompany;
	private String stockType;
	private double currentBalance;
	private double rate;
	private double amount;

}
