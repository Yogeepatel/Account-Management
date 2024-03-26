package com.yogi.account.Account.service;

public interface AccountManagementService {

    String addElssStatementData();

    String fetchSbiStatementData(String task) throws Exception;

    String fetchUbiStatementData(String task) throws Exception;
}
