package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "STOCKS")
public class Stocks {
	
	@MongoId
	 String id;
	 String stockName;
	 String stockCompany;
	 String stockType;
	 double currentBalance;
	 double rate;
	 double amount;

}
