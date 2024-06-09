package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Document(collection = "BankDetails")
public class BankDetails {
    @MongoId
     String id;
     String name;
     String accountNumber;
     String ifscCode;
     String branch;
     String address;
     String password;
     String micrCode;
     String customerId;
    
     String panNumber;
     String dpId;
     String boStatus;
     String accountStatus;
   

    public List<String> bankDetailsTableList() {
        List<String> bankDetailsTableFormat = new ArrayList<>();
        bankDetailsTableFormat.add("name");
        bankDetailsTableFormat.add("Account Number");
        bankDetailsTableFormat.add("ifsc");
        bankDetailsTableFormat.add("branch name");
        bankDetailsTableFormat.add("address");
        bankDetailsTableFormat.add("micr");
        bankDetailsTableFormat.add("Customer ID");
        bankDetailsTableFormat.add("password");
        return bankDetailsTableFormat;
    }

    
}
