package com.yogi.account.Account.service;

public interface PasswordService {
	Object getpasswordhelper();
	
	String login(String username, String password);
	
	String validateKey(int receivedKey);
}
