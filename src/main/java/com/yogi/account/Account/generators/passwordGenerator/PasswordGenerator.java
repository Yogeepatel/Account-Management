package com.yogi.account.Account.generators.passwordGenerator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

@Component
public class PasswordGenerator {
		private static String orignalPassword;
	private static String passwordHelper;
	
	private static void generatePassword() throws IllegalAccessException {
		Field[] keyList = Constants.class.getFields();
		int keyListSize = keyList.length;
		Random random = new Random();
		char[] phnoArray =Constants.MOBILENUMBER.toCharArray();
		
		List<String> firstLast = List.of("First", "Last");
		Integer firstlastindex = random.nextInt(0, firstLast.size());
		int index = random.nextInt(0, keyListSize - 6);
		String nameKey = keyList[index].getName();
		String nameValue = keyList[index].get(nameKey).toString();
		Integer length = random.nextInt(3,nameValue.length());
		String name = nameValue.substring(0, length );
		
		int phnoLength = random.nextInt(1, 10);
		char[] phno = firstlastindex == 0 ? ArrayUtils.subarray(phnoArray, 0, phnoLength):
				ArrayUtils.subarray(phnoArray,  10 - phnoLength , 11);
		String phnoValue ="";
		for (char c : phno) {
			phnoValue = StringUtils.join(phnoValue, c);
		}
		
		Integer dayMonth = random.nextInt(keyListSize-5, keyListSize-1);
		String dayMonthKey = keyList[dayMonth].getName();
		String dayMonthValue = keyList[dayMonth].get(keyList[dayMonth].getName()).toString();
		dayMonthValue = dayMonthValue.substring(4,8);
		

		Integer year = random.nextInt(keyListSize-5, keyListSize-1);
		String yearKey = keyList[year].getName();
		String yearValue = keyList[year].get(yearKey).toString();
		yearValue = yearValue.substring(0,4);
		

		 orignalPassword = StringUtils.join(name, phnoValue, dayMonthValue, yearValue);

		 passwordHelper = StringUtils.join("First ", length, " letters of ", keyList[index].getName().replace("_", " "), "  and Phone Number's ", firstLast.get(firstlastindex)," ", phnoLength, " digit's ", " and mm//dd of ",dayMonthKey," and yyyy ",yearKey);
		System.out.println(orignalPassword);
		System.out.println(passwordHelper);
	}
	
	public Object classAccess(String password,boolean StringHelper) {
		
		if (!StringUtils.isBlank(password)) {
				return checkPassword(password);
		}
		if (StringHelper) {
			try {
				generatePassword();
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e.getMessage());
			}
			return passwordHelper;
		}
		return null;
	}
		
		private boolean checkPassword(String tocheck) {
			return tocheck.equals(orignalPassword);
		}
}
