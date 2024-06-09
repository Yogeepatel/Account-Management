package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Document(collection = "ELSS")
public class ELSS {

    @MongoId
     String id;
     String purchaseDate;
     String nameOfFund;
     String folioNumber;
     Double units;
     String amount;
    


}
