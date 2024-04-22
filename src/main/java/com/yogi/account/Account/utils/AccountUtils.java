package com.yogi.account.Account.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

public class AccountUtils {

    public static PDDocument copyDocument(String fileName, String password) {
	    try {
		    PDDocument document = Loader.loadPDF(new File(fileName),password);
            document.setAllSecurityToBeRemoved(true);
            File tempFile= File.createTempFile("tempFile",".pdf",new File("src/main/resources/modifiedPdfs"));
            String fileAbsolutePath = tempFile.getAbsolutePath();
            document.save(tempFile);
		    PDDocument documentObject = Loader.loadPDF(new File(fileAbsolutePath));
			tempFile.delete();
            return  documentObject;
	    } catch (IOException e) {
		    throw new RuntimeException(e);
	    }
    }
	
	
}
