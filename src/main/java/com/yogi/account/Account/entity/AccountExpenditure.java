package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;


@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Document(collection = "Expenditure")
public class AccountExpenditure {
    @MongoId
     String id;
     String month;
     int moneyAdded;
     double openingBalance;
     double closingBalance;
     double expenditureFromAccount;
}
