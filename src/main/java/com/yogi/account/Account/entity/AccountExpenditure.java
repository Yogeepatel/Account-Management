package com.yogi.account.Account.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "Expenditure")
public class AccountExpenditure {
    @MongoId
    private String id;
    private String openingBalance;
    private String closingBalance;
}
