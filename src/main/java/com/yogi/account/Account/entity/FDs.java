package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@Document(collection = "FDs")
public class FDs {

    @MongoId
     String id;
     String type;
     String number;
     String amount;
     String accountOpenDate;
     String Roi;
     String bankName;
     String maturityDate;

    public List<String> fdTableList() {
        List<String> fdTableFormat = new ArrayList<>();
        fdTableFormat.add("id");
        fdTableFormat.add("bankName");
        fdTableFormat.add("type");
        fdTableFormat.add("number");
        fdTableFormat.add("accountOpenDate");
        fdTableFormat.add("Roi");
        fdTableFormat.add("maturityDate");
        fdTableFormat.add("amount");
        return fdTableFormat;
    }

    public Map<String, Integer> fdMapper(List<String> text) {
        List<String> fdFormat = fdTableList();
        Map<String, Integer> map = new HashMap<>();
        for (int j = 2; j < fdFormat.size(); j++) {
            for (int k = 0; k < text.size(); k++) {
                if (text.get(k).toLowerCase().contains(fdFormat.get(j).toLowerCase())) {
                     map.put(fdFormat.get(j), k);
                    break;
                }
            }
        }
        return map;
    }
}
