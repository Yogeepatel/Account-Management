package com.yogi.account.Account.generators.passwordGenerator;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SecretKeyGenerator {
	
	private static int secretKey;
	public Object classAccess(int secretKey) {
		if (ObjectUtils.isNotEmpty(secretKey) && secretKey == 0) {
			return generateKey();
		}
		return validateKey(secretKey);
	}
	
	private static int generateKey() {
		Random random = new Random();
		int passcodeDigit1 = random.nextInt(0,9);
		int passcodeDigit2 = random.nextInt(0,9);
		int passcodeDigit3 = random.nextInt(0,9);
		int passcodeDigit4 = random.nextInt(0,9);
		 secretKey = (passcodeDigit1*1000)+(passcodeDigit2*100)+(passcodeDigit3*10)+passcodeDigit4;
		return secretKey;
	}
	
	private boolean validateKey(int receivedKey) {
		if (secretKey == receivedKey) {
			return true;
		} else {
			return false;
		}
	}
}
