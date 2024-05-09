package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Document(collection = "SIP")
public class Sip {
	
	@MongoId
	 String id;
	 String companyName;
	 String sipName;
	 String fundType;
	 double currentBalance;
	 double freeBalance;
	 double rate;
	 double amount;
	 double monthlyOpeningBalance;
	 double monthlyClosingBalance;
}
