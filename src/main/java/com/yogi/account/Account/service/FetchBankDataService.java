package com.yogi.account.Account.service;

public interface FetchBankDataService {
    String fetchSbiStatementData(String task) throws Exception;

    String fetchUbiStatementData(String task) throws Exception;
}
