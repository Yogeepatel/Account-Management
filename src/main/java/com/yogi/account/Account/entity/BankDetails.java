package com.yogi.account.Account.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "BankDetails")
public class BankDetails {
    @MongoId
    private String id;
    private String name;
    private String accountNumber;
    private String ifscCode;
    private String branch;
    private String address;
    private String password;
    private String micrCode;
    private String customerId;
    
    private String pan_Number;
    private String dp_Id;
    private String bo_Status;
    private String account_Status;
   

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

    public Map<String, Integer> bankDetailsMapperTable(String[] text) {
        List<String> fdFormat = bankDetailsTableList();
        Map<String, Integer> map = new HashMap<>();
        for (int j = 1; j < fdFormat.size()-1; j++) {
            for (int k = 0; k < text.length; k++) {
                if (text[k].toLowerCase().contains(fdFormat.get(j).toLowerCase())) {
                    map.put(fdFormat.get(j), k);
                }
            }
        }
        return map;
    }
}
