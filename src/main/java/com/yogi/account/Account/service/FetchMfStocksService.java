package com.yogi.account.Account.service;

public interface FetchMfStocksService {
	String addElssStatementData();
	
	void generateStocksMfData();
	
	String saveSipandMf();
	String saveAccountDetails();
	
	String saveStocks();
}
