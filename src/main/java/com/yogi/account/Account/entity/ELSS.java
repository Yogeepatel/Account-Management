package com.yogi.account.Account.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ELSS")
public class ELSS {

    @MongoId
    private String id;
    private String Purchase_Date;
    private String Name_of_fund;
    private String Folio_number;
    private Double Units;
    private String Amount;
}
