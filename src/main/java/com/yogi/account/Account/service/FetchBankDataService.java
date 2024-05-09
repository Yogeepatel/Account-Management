package com.yogi.account.Account.service;

public interface FetchBankDataService {
    void fetchSbiStatementData() ;

    void fetchUbiStatementData() ;
    
    String saveBankDetailsofUBI();
	
	String saveBankDetailsofIcici();
	
	void fetchIciciStatementData();
	
	String saveBankDetailsofSBI();
    
    String collectFdDataofSBI() ;
    
    String collectFdDataofUBI() ;
    
    String expenditureDataofSBI();
    
    String expenditureDataofUBI();
	
	String expenditureDataofIcici();
	
	String salaryUsage();
	
	String yearlyExpenditure();
}
