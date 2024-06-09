package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Document(collection = "YearExpenditure")
public class YearlyExpenditure {
	String id;
	double spendFromSbi;
	double spendFromUbi;
	double spendFromIcici;
	double total;
}
