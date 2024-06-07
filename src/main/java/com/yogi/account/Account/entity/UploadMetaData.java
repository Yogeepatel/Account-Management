package com.yogi.account.Account.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Document(collection = "FileUploadMetadata")
public class UploadMetaData {
	String id;
	int totalFileRequied;
	int pdfRequired;
	int csvRequired;
	Map<String,String> attachmentTitle;
}
