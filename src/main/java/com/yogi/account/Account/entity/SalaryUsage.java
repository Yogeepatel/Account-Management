package com.yogi.account.Account.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "SalaryUsage")
public class SalaryUsage {
    @MongoId
    private String id;
    private String date;
    private String reason;
    private String deposits;
    private String withdrawals;
    private String balance;
}
