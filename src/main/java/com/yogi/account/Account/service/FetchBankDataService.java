package com.yogi.account.Account.service;

public interface FetchBankDataService {
    void fetchSbiStatementData(String filename) ;
	
	void fetchIciciStatementData(String tabularFileName, String nonTabularFileName);
	
	void fetchUbiStatementData(String filename) ;
    
    String saveBankDetailsofUBI();
	
	String saveBankDetailsofIcici();
	
	
	String saveBankDetailsofSBI();
    
    String collectFdDataofSBI() ;
    
    String collectFdDataofUBI() ;
    
    String expenditureDataofSBI(String monthSelected);
    
    String expenditureDataofUBI(String monthSelected);
	
	String expenditureDataofIcici(String monthSelected);
	
	String salaryUsage(String month);
	
	String yearlyExpenditure();
}
