package com.yogi.account.Account.generators.emailGenerator;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import static jakarta.mail.Message.RecipientType.TO;

@Component
@AllArgsConstructor
public class EmailGenerator {
	private Gmail service;
	
	@Autowired
	private Environment environment;
	private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	public EmailGenerator() throws Exception {
		NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		service = new Gmail.Builder(HTTP_TRANSPORT, jsonFactory, getCredentials(HTTP_TRANSPORT)).setApplicationName("email").build();
	}
	
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
		// Load client secrets.
		InputStream in = EmailGenerator.class.getResourceAsStream("/static/client_secret_434177171725-jdu43gve2ah971e5110bjri9v26hqies.apps.googleusercontent.com.json");
		
		if (in == null) {
			throw new FileNotFoundException("Resource not found: client Secret file");
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND)).setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile())).setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}
	
	public void sendMail(String messageSubject, Map<String, String> bodyText, String path, boolean destruct) throws Exception {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session);
		
		email.setFrom(new InternetAddress(Objects.requireNonNull(environment.getProperty("email.senderEmailId"))));
		System.out.println(environment.getProperty("email.senderEmailId"));
		email.addRecipient(TO, new InternetAddress(Objects.requireNonNull(environment.getProperty("email.receiverEmailId"))));
		if (destruct) {
			email.addRecipient(TO, new InternetAddress(Objects.requireNonNull(environment.getProperty("email.alternativeEmailId"))));
		}
		email.setSubject(messageSubject);
		Multipart multipart = new MimeMultipart();
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(bodyText.get("bodyText"), bodyText.get("contentType"));
		multipart.addBodyPart(mimeBodyPart);
		if (!StringUtils.isBlank(path)) {
			MimeBodyPart attachment = new MimeBodyPart();
			attachment.attachFile(new File(path));
			attachment.setFileName(new File(path).getName());
			multipart.addBodyPart(attachment);
			
		}
		email.setContent(multipart);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		email.writeTo(buffer);
		byte[] rawMessageBytes = buffer.toByteArray();
		String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
		Message message = new Message();
		message.setRaw(encodedEmail);
		try {
			message = service.users().messages().send(environment.getProperty("email.senderEmailId"), message).execute();
			System.out.println("Message id: " + message.getId());
			System.out.println(message.toPrettyString());
		} catch (GoogleJsonResponseException e) {
			GoogleJsonError error = e.getDetails();
			if (error.getCode() == 403) {
				System.err.println("Unable to send message: " + e.getDetails());
			} else {
				throw e;
			}
		}
	}
}
