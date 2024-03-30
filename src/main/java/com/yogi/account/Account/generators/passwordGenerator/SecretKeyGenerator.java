package com.yogi.account.Account.generators.passwordGenerator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SecretKeyGenerator {
	
	private static String secretKey;
	public Object classAccess(String secretKey) {
		if (StringUtils.isBlank(secretKey)) {
			return generateKey();
		}
		return validateKey(secretKey);
	}
	
	private static String generateKey() {
		Random random = new Random();
		Integer passcodeDigit1 = random.nextInt(0,9);
		Integer passcodeDigit2 = random.nextInt(0,9);
		Integer passcodeDigit3 = random.nextInt(0,9);
		Integer passcodeDigit4 = random.nextInt(0,9);
		 secretKey = StringUtils.join(passcodeDigit1.toString(),passcodeDigit2.toString(),passcodeDigit3.toString(),passcodeDigit4.toString());
		return secretKey;
	}
	
	private boolean validateKey(String receivedKey) {
		if (secretKey.equals(receivedKey)) {
			return true;
		} else {
			return false;
		}
	}
}
