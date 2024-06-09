package com.yogi.account.Account.utils;

import com.yogi.account.Account.entity.UploadMetaData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountUtils {
	
	public static UploadMetaData accountExpenditureFileUploadMetaData(List<String> attachmentTitleKey){
		
			Map<String,String> titles = new HashMap<>();
			if (!attachmentTitleKey.contains("SBI")){
				titles.put("File"+(titles.keySet().size()+1),"Upload SBI Pdf Statement");
			}
			if (!attachmentTitleKey.contains("UBI")){
				titles.put("File"+(titles.keySet().size()+1),"Upload UBI Pdf Statement");
			}
			if (!attachmentTitleKey.contains("ICICI")){
				titles.put("File"+(titles.keySet().size()+1),"Upload Non-Tabulated ICICI Pdf Statement");
				titles.put("File"+(titles.keySet().size()+1),"Upload Tabulated ICICI Pdf Statement");
			}
			
			return new UploadMetaData("monthly",titles.keySet().size(),titles.keySet().size(),0,titles);

	}
	
	public static UploadMetaData bankAccounkBulkUpdateFileUploadMetaData(List<String> attachmentTitleKey){
		
		Map<String,String> titles = new HashMap<>();
		if (attachmentTitleKey.contains("State Bank of India")){
			titles.put("File"+(titles.keySet().size()+1),"Upload SBI Pdf Statement");
		}
		if (attachmentTitleKey.contains("Union Bank of India")){
			titles.put("File"+(titles.keySet().size()+1),"Upload UBI Pdf Statement");
		}
		if (attachmentTitleKey.contains("ICICI")){
			titles.put("File"+(titles.keySet().size()+1),"Upload Non-Tabulated ICICI Pdf Statement");
			titles.put("File"+(titles.keySet().size()+1),"Upload Tabulated ICICI Pdf Statement");
		}
		
		return new UploadMetaData("monthly",titles.keySet().size(),titles.keySet().size(),0,titles);
		
	}
	
public static UploadMetaData monthExpenditureFileUploadMetaData(){

		return new UploadMetaData("monthly",4,4,0,Map.of("File1","Upload Non-Tabulated ICICI Pdf Statement","File2","Upload SBI Pdf Statement","File3","Upload UBI Pdf Statement","File4","Upload Tabulated ICICI Pdf Statement"));
}

public  static  UploadMetaData salaryUsageFileUploadMetaData(){
	return  new UploadMetaData("salaryUsage",2,2,0, Map.of("File1","Upload Non-Tabulated ICICI Pdf Statement","File2","Upload Tabulated ICICI Pdf Statement"));
	
}
	
	public static UploadMetaData elssFileUploadMetaData(){
		
		Map<String,String> titles = new HashMap<>();
		titles.put("file1","Upload ELSS Holding Pdf File");
		
		return new UploadMetaData("elss",titles.keySet().size(),titles.keySet().size(),0,titles);
		
	}
	
	public static UploadMetaData investmentAccountFileUploadMetaData(){
		
		Map<String,String> titles = new HashMap<>();
		titles.put("file1","Upload All Holding Pdf File");
		titles.put("file2","Upload All Holdings Csv File");
		return new UploadMetaData("invesments",titles.keySet().size(),titles.keySet().size()-1,1,titles);
		
	}
	
	

}
