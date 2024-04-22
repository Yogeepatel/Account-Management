package com.yogi.account.Account.service;

public interface FetchBankDataService {
    void fetchSbiStatementData() ;

    void fetchUbiStatementData() ;
    
    String saveBankDetailsofUBI();
    
    String saveBankDetailsofSBI();
    
    String collectFdDataofSBI() ;
    
    String collectFdDataofUBI() ;
    
    String ExpenditureDataofSBI();
    
    String ExpenditureDataofUBI();
}
