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

    public List<String> bankDetailsTableList() {
        List<String> fdTableFormat = new ArrayList<>();
        fdTableFormat.add("name");
        fdTableFormat.add("Account Number");
        fdTableFormat.add("ifsc");
        fdTableFormat.add("branch name");
        fdTableFormat.add("address");
        fdTableFormat.add("micr");
        fdTableFormat.add("Customer ID");
        fdTableFormat.add("password");
        return fdTableFormat;
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
