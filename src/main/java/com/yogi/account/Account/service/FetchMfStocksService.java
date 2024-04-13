package com.yogi.account.Account.service;

public interface FetchMfStocksService {
	String addElssStatementData();
	
	void fetchStocksAndMfMetaData();
	
	void fetchStocksAndMfTransactionStatement();
	void saveSipandMf();
	void saveAccountDetails();
}
