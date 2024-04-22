package com.yogi.account.Account.ExcelReader;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
	
	public static Map<String, List<?>> readExcel(FileInputStream file,int sheetNumber) {
		try {
			
			Map<String, List<?>> data = new LinkedHashMap<>();
			
			Workbook workbook = WorkbookFactory.create(file);
			Sheet sheet = workbook.getSheetAt(sheetNumber);
			for (Row row : sheet) {
				int i = 0;
				String key = "";
				List<Object> valuesList = new ArrayList<>();
				for (Cell cell : row) {
					if (ObjectUtils.isNotEmpty(cell))
						if (i == 0) {
							key = String.valueOf(cell);
							
						}
					if ((i > 0 && i < 4) || i == 7) {
						String cellType = String.valueOf(cell.getCellType());
						if (cellType.equalsIgnoreCase("NUMERIC")) {
							valuesList.add(cell.getNumericCellValue());
						}
						if (cellType.equalsIgnoreCase("STRING")) {
							valuesList.add(cell.getStringCellValue());
						}
						if (cellType.equalsIgnoreCase("BOOLEAN")) {
							valuesList.add(cell.getBooleanCellValue());
						}
					}
					i=i+1;
				}
				
				i = 0;
				if (ObjectUtils.isNotEmpty(valuesList)) {
					data.put(key, valuesList);
				}
			}
			return data;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
