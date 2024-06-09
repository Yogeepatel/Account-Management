package com.yogi.account.Account.service;

public interface FetchMfStocksService {
	String addElssStatementData();
	
	void fetchStocksAndMfMetaData();
	
	void fetchStocksAndMfTransactionStatement();
	
	String saveSipandMf();
	String saveAccountDetails();
	
	String saveStocks();
}
