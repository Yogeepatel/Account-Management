package com.yogi.account.Account.service;

public interface FetchMfStocksService {
	String addElssStatementData(String filename);
	
	void readstocksCsvFile(String filename);
	
	void readMfCsvFile(String filename);
	
	void fetchStocksAndMfMetaData(String filename);
	
	void fetchStocksAndMfTransactionStatement(String filename);
	
	String saveSipandMf();
	String saveAccountDetails();
	
	String saveStocks();
}
