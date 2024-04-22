package com.yogi.account.Account.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ELSS")
public class ELSS {

    @MongoId
    private String id;
    private String purchase_Date;
    private String name_of_fund;
    private String folio_number;
    private Double units;
    private String amount;
    


}
