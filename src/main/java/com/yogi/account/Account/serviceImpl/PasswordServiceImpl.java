package com.yogi.account.Account.serviceImpl;

import com.yogi.account.Account.generators.emailGenerator.EmailGenerator;
import com.yogi.account.Account.generators.passwordGenerator.PasswordGenerator;
import com.yogi.account.Account.generators.passwordGenerator.SecretKeyGenerator;
import com.yogi.account.Account.service.PasswordService;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class PasswordServiceImpl implements PasswordService {
	private  PasswordGenerator passwordGenerator;
	private Environment environment;
	private SecretKeyGenerator secretKeyGenerator;
	private EmailGenerator emailGenerator;
	
	
	private static long time;
	@Override
	public Object getpasswordhelper() {
		return passwordGenerator.classAccess(null,true);
	}
	
	@Override
	public String login(String username, String password) {
		if (usernameCheck(username)) {
			boolean login = (boolean) validatePassword(password);
			if (login) {
				try {
					sendKeyInMail();
					time = System.currentTimeMillis();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return "Login Successful";
			}
			else {
				return "Wrong Credentials";
			}
		}
		return "Wrong Username";
	}
	
	@Override
	public String validateKey(int receivedKey) {
		 long currentTime = System.currentTimeMillis();
		if (currentTime - time < 120000) {
			boolean check =  (boolean) secretKeyGenerator.classAccess(receivedKey);
			if (check){
				return "Secret Key verified";
			}
			else {
				return  "Invalid Secret Key";
			}
		}
		return "Timeout";
	}
	
	private Object validatePassword(String password) {
		return passwordGenerator.classAccess(password,false);
	}
	
	
	private boolean usernameCheck(String username) {
		return StringUtils.equals(environment.getProperty("service.username"), username);
	}
	
	private void sendKeyInMail() throws Exception {
		Multipart multipartMessage = new MimeMultipart();
		multipartMessage.addBodyPart(new MimeBodyPart());
		
		String messageSubject = "Secret Key";
		String bodyText =StringUtils.join( "<!DOCTYPE html>\n" +
				"<html lang=\"en\">\n" +
				"<head>\n" +
				"    <meta charset=\"UTF-8\">\n" +
				"    <title>App Login</title>\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Secret Key</h1>\n" +
				"<p>Your Secret Key is : " ,secretKeyGenerator.classAccess(0),"</p>\n" +
				"<br/>\n" +
				"<br/>\n" +
				"<p style=\"text-align:center\"><strong>If You have not logged in, secure your data with below link </strong></p>\n" +
				"<p style=\"color:blue;text-decoration:none;text-align:center\"><a href=\"",environment.getProperty("destruct.url" +
				""),"\">INVALID LOGIN</a></p>\n" +
				"</body>\n" +
				"</html>\n");
		Map<String,String> bodyTextMap = Map.of("bodyText",bodyText,"contentType","text/html");
		emailGenerator.sendMail(messageSubject, bodyTextMap, null, false);
		
	}
}
