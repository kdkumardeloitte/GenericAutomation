package com.deloitte.rpa.octa.dataHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Reads data from excel input file
 */
public class InputDataHandler {

	private Workbook dataWorkbook;
	private static DataFormatter df = new DataFormatter();

	public void loadDataWorkbook(String dataFileName) throws EncryptedDocumentException, InvalidFormatException, IOException {
		InputStream inputSteam = new FileInputStream(dataFileName);
		dataWorkbook = WorkbookFactory.create(inputSteam);
	}

	public List<Map<String, String>> getDataForKey(String requiredKey, String sheetName) {
		List<Map<String, String>> rows = new ArrayList<>();
		Sheet sheet;
		if(sheetName!=null && !sheetName.isEmpty())
			sheet = dataWorkbook.getSheet(sheetName);
		else
			sheet = dataWorkbook.getSheetAt(0);
		if(sheet==null)
			return rows;
		int totalRowCount = sheet.getLastRowNum();
		int noOfColumns = sheet.getRow(1).getLastCellNum();

		for (int i = 3; i <= totalRowCount; i++) {
			boolean flag = false;
			Map<String, String> map = new LinkedHashMap<>();
			for (int j = 0; j < noOfColumns; j++) {
				String key = df.formatCellValue(sheet.getRow(1).getCell(j)).trim();
				if(requiredKey!=null && j==1) {
					String referralKey = df.formatCellValue(sheet.getRow(i).getCell(j)).trim();
					if(referralKey.equals(requiredKey))
						flag = true;
					else
						break;
				}
				String value = df.formatCellValue(sheet.getRow(i).getCell(j)).trim();
				map.put(key, value);
			}
			if(flag || requiredKey==null)
				rows.add(map);
		}
		return rows;
	}
}