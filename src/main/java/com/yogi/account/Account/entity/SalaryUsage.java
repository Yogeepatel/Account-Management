package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "SalaryUsage")
public class SalaryUsage {
    @MongoId
     String id;
     
    int self ;
 int dad;
 int mom;
 int  brother;
 int  fixedDeposits;
 int   mutualFunds;
 int stocks;
 int  security;
 int  others;
}
