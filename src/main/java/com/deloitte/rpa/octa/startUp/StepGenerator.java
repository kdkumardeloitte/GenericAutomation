package com.deloitte.rpa.octa.startUp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.deloitte.rpa.octa.OctaSuiteContext;
import com.deloitte.rpa.octa.constants.SpecialOperationTypeConstants;
import com.deloitte.rpa.octa.dataHandler.InputDataHandler;
import com.deloitte.rpa.octa.dataHandler.OutputDataHandler;
import com.deloitte.rpa.octa.factories.RandomValueGenerator;
import com.deloitte.rpa.octa.reporter.HtmlExtentReporter;

public class StepGenerator implements SpecialOperationTypeConstants {

	private final static Logger logger = Logger.getLogger(StepGenerator.class);

	private InputDataHandler dataReaderTest = new InputDataHandler();
	private Workbook stepWorkbook;
	private static DataFormatter df = new DataFormatter();

	private static final String SCRIPT_PATH = "resources/script/";
	private static final String INPUT_PATH = "resources/data/";
	private static final String LIST_PATH = "resources/listOfScript/";
	private static String inputFileToUse;
	private static String outputFileName;
	public static Properties outputValues = new Properties();
	private static boolean ifConditionStarted = false;
	private static boolean elseConditionStarted = false;
	private static int endOfIf;
	private static int endOfElse;
	private Map<String, String> allValuesFromInputFile = new LinkedHashMap<>();

	public static String getOutputFileName() {
		return outputFileName;
	}

	public void resetIfConditionVariables() {
		ifConditionStarted = false;
		elseConditionStarted = false;
		endOfIf = 0;
		endOfElse = 0;
		StepExecutor.conditionResult = null;
		logger.info("End of IfCondition");
	}

	public void ifConditionStarter(String directValue) throws Exception {
		logger.info("Begining of IfCondition");
		ifConditionStarted = true;
		if(directValue.matches("^Step:[0-9]+-[0-9]+\\|\\|Step:[0-9]+-[0-9]+$")) {
			Pattern r = Pattern.compile("^Step:[0-9]+-([0-9]+)\\|\\|Step:[0-9]+-([0-9]+)$");
			Matcher m = r.matcher(directValue);
			if (m.find( )) {
				endOfIf = Integer.valueOf(m.group(Integer.valueOf(1)));
				logger.info("End Of If at "+endOfIf);
				endOfElse = Integer.valueOf(m.group(Integer.valueOf(2)));
				logger.info("End Of Else at "+endOfElse);
			}
		}else {
			throw new Exception("Invalid synatx for IfCondition provided");
		}
	}

	public int whenNoValueForLoop(int excelrow, Sheet sheet) {
		int i = excelrow;
		int repeatCount = 1;
		while(repeatCount!=0) {
			String specialOperation = df.formatCellValue(sheet.getRow(i).getCell(1)).trim();
			if(specialOperation.equals("Repeat]")) {
				--repeatCount;
			}else if(specialOperation.equals("Repeat[")){
				++repeatCount;
			}
			i++;
		}
		return i-1;
	}

	public int ifConditionChecker(int index) {
		if(ifConditionStarted && StepExecutor.conditionResult!=null) {
			if(StepExecutor.conditionResult) {
				if(index>endOfIf) {
					index = endOfElse+1;
					resetIfConditionVariables();
				}
			}else {
				if(!elseConditionStarted) {
					elseConditionStarted = true;
					index=endOfIf+1;
				}if(index>endOfElse) {
					resetIfConditionVariables();
				}
			}
		}
		return index;
	}

	public int stepRepeator(int excelrow, String sheetName, String requiredKey, Sheet sheet, Map<String, String> xpathMap) throws Exception {
		logger.info("Excel row: "+excelrow+"|SheetName: "+sheetName);
		int endRow = excelrow;
		int i = excelrow;
		List<Map<String, String>> rows = dataReaderTest.getDataForKey(requiredKey, sheetName);
		int times = rows.size();
		StepExecutor stepExecutor = new StepExecutor();
		boolean indexFlag = false;
		if(times==0) {
			return whenNoValueForLoop(excelrow,sheet);
		}
		while(times>0) {
			i = ifConditionChecker(i);
			Map<String, String> map = rows.get(rows.size()-times);
			allValuesFromInputFile.putAll(map);
			String stepNo = df.formatCellValue(sheet.getRow(i).getCell(0)).trim();
			String specialOperation = df.formatCellValue(sheet.getRow(i).getCell(1)).trim();
			String operationType = df.formatCellValue(sheet.getRow(i).getCell(2)).trim();
			String directValue = df.formatCellValue(sheet.getRow(i).getCell(3)).trim();
			String sheetOrColumnName = df.formatCellValue(sheet.getRow(i).getCell(4)).trim();
			String xpathKey = df.formatCellValue(sheet.getRow(i).getCell(5)).trim();
			String timeOut = df.formatCellValue(sheet.getRow(i).getCell(6)).trim();
			String stepDescription = df.formatCellValue(sheet.getRow(i).getCell(7)).trim();

			if(specialOperation.startsWith(COMPONENT)) {
				new StepGenerator().startUp(sheetOrColumnName);
				continue;
			}
			if(specialOperation.equals(IF_CONDITION)) {
				ifConditionStarter(directValue);
			}else if(specialOperation.equals(REPEAT_END)) {
				endRow = i;
				i = excelrow;
				--times;
				logger.info("Final Map at end of loop: " + allValuesFromInputFile);
				logger.info(times+"--------------------"+excelrow);
				if(indexFlag) {
					stepExecutor.setIndex(1+stepExecutor.getIndex());
					logger.info("Incrementing index: "+stepExecutor.getIndex());
					indexFlag=false;
				}
				continue;
			}else if(specialOperation.equals(REPEAT_START)){
				int tempIndex = stepExecutor.getIndex();
				stepExecutor.setIndex(0);
				i = stepRepeator(i+1, sheetOrColumnName, map.get("Key"), sheet, xpathMap);
				stepExecutor.setIndex(tempIndex);
			}else if(!specialOperation.equals(REPEAT_END)) {
				//				String fileValue = map.get(sheetOrColumnName);
				String fileValue = allValuesFromInputFile.get(sheetOrColumnName);

				if(!outputValues.isEmpty() && outputValues.containsKey(sheetOrColumnName)) {
					fileValue = outputValues.getProperty(sheetOrColumnName);
				}else if(fileValue != null && !fileValue.isEmpty() && fileValue.contains("#")) {
					fileValue = new RandomValueGenerator().getRandomValue(fileValue); 
					outputValues.setProperty(sheetOrColumnName, fileValue);
					OutputDataHandler.saveProperties(outputValues);
					OutputDataHandler.loadProperties(outputValues);
				}

				logger.info(stepNo+"|"+specialOperation+"|"+operationType+"|"+directValue+"|"+fileValue+"|"+
						xpathKey+"|"+timeOut+"|"+stepDescription);
				stepExecutor.stepExecutor(xpathMap, specialOperation, operationType, directValue, fileValue, xpathKey, timeOut, stepDescription);
			}
			if(specialOperation.equalsIgnoreCase(REPLACE_WITH_LINE_INDEX)) {
				indexFlag = true;
			}
			i++;
		}
		stepExecutor.setIndex(0);
		return endRow;
	}

	public void startUp(String stepFileName) throws Exception {
		stepFileName = SCRIPT_PATH + stepFileName;
		InputStream inputSteam = new FileInputStream(stepFileName);
		stepWorkbook = WorkbookFactory.create(inputSteam);
		StepExecutor stepExecutor = new StepExecutor();
		Set<String> setOfElementKeys = stepExecutor.getElementKeys(stepFileName);
		Map<String, String> xpathMap = stepExecutor.getRequiredXpathMap(stepFileName, setOfElementKeys);
		Sheet sheet = stepWorkbook.getSheet("Element-Operations");
		int totalRowCount = sheet.getLastRowNum();
		Map<String, String> genericValues = new LinkedHashMap<>();
		boolean indexFlag = false;

		String inputFileName;
		logger.info("inputFileToUse: "+inputFileToUse);
		if(inputFileToUse==null) {
			File stepFile = new File(stepFileName);
			inputFileName = INPUT_PATH + stepFile.getName().replace("_Script", "_Input");
		}else {
			inputFileName = INPUT_PATH + inputFileToUse;
			inputFileToUse = null;
		}

		logger.info("Looking for input file: "+inputFileName);
		File inputFile = new File(inputFileName);
		if(inputFile.exists()) {
			dataReaderTest.loadDataWorkbook(inputFileName);
			List<Map<String, String>> rows = dataReaderTest.getDataForKey(null, null);
			genericValues = rows.get(0);
			allValuesFromInputFile.putAll(genericValues);
			logger.info("Final Map at begining of script: " + allValuesFromInputFile);
		}

		for (int i = 1; i <= totalRowCount; i++) {
			i = ifConditionChecker(i);

			String stepNo = df.formatCellValue(sheet.getRow(i).getCell(0)).trim();
			String specialOperation = df.formatCellValue(sheet.getRow(i).getCell(1)).trim();
			String operationType = df.formatCellValue(sheet.getRow(i).getCell(2)).trim();
			String directValue = df.formatCellValue(sheet.getRow(i).getCell(3)).trim();
			String sheetOrColumnName = df.formatCellValue(sheet.getRow(i).getCell(4)).trim();
			String xpathKey = df.formatCellValue(sheet.getRow(i).getCell(5)).trim();
			String timeOut = df.formatCellValue(sheet.getRow(i).getCell(6)).trim();
			String stepDescription = df.formatCellValue(sheet.getRow(i).getCell(7)).trim();

			if(specialOperation.startsWith(COMPONENT)) {
				if(specialOperation.equals(COMPONENT_LOGIN)) {
					List<Map<String, String>> rows = dataReaderTest.getDataForKey(null, "Login");
					if(!rows.isEmpty()) {
						logger.info("map is not empty"+ rows);
						Map<String, String> map = rows.get(0);
						inputFileToUse = map.get("Login File Name");
						allValuesFromInputFile.putAll(map);
						logger.info("Final Map adding login file: " + allValuesFromInputFile);
					}
				}
				new StepGenerator().startUp(sheetOrColumnName);
				continue;
			}
			if(specialOperation.equals(IF_CONDITION)) {
				ifConditionStarter(directValue);
			}else if(specialOperation.equals(REPEAT_START)) {
				int tempIndex = stepExecutor.getIndex();
				stepExecutor.setIndex(0);
				i = stepRepeator(i+1, sheetOrColumnName, null, sheet, xpathMap);
				stepExecutor.setIndex(tempIndex);
			}else if(!specialOperation.equals(REPEAT_END)) {
				//				String fileValue = genericValues.get(sheetOrColumnName);
				String fileValue = allValuesFromInputFile.get(sheetOrColumnName);

				if(!outputValues.isEmpty() && outputValues.containsKey(sheetOrColumnName)) {
					fileValue = outputValues.getProperty(sheetOrColumnName);
				}else if(fileValue != null && !fileValue.isEmpty() && fileValue.contains("#")) {
					fileValue = new RandomValueGenerator().getRandomValue(fileValue); 
					outputValues.setProperty(sheetOrColumnName, fileValue);
					OutputDataHandler.saveProperties(outputValues);
					OutputDataHandler.loadProperties(outputValues);
				}

				logger.info(stepNo+"|"+specialOperation+"|"+operationType+"|"+directValue+"|"+fileValue+"|"+
						xpathKey+"|"+timeOut+"|"+stepDescription);
				stepExecutor.stepExecutor(xpathMap, specialOperation, operationType, directValue, fileValue, xpathKey, timeOut, stepDescription);
			}else if(specialOperation.equals(REPEAT_END)){
				if(indexFlag) {
					stepExecutor.setIndex(1+stepExecutor.getIndex());
					indexFlag=false;
				}
			}
			if(specialOperation.equalsIgnoreCase(REPLACE_WITH_LINE_INDEX))
				indexFlag = true;
		}
	}

	public static void postSteps(String testName) throws IOException {
		OctaSuiteContext.writeToExcel(testName, "Test Step Name", "Status", "Values");
		//OctaSuiteContext.deleteScreenshots();
	}

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException, InterruptedException {
		//String listOfScript = "MedifastScripts.txt";
		String listOfScript = "MyClient_Apr_Test_Batch.txt";
		List<String> lines = new ArrayList<>();

		if(args!=null && args.length>0) {
			listOfScript = "Test_Summary_Report.txt";
			for (String script : args) {
				if(script.endsWith(".txt")) {
					List<String> scriptList = FileUtils.readLines(new File(LIST_PATH+script), "utf-8");
					if(scriptList!=null && !scriptList.isEmpty())
						lines.addAll(scriptList);
				} else if(script.endsWith(".xlsx")) {
					lines.add(script);
				}
			}
		}
		else if(listOfScript.endsWith(".txt"))
			lines = FileUtils.readLines(new File(LIST_PATH+listOfScript), "utf-8");
		else if(listOfScript.endsWith(".xlsx")) {
			lines.add(listOfScript);
		}

		logger.info("Scripts to be executed: "+lines);
		HtmlExtentReporter.startReport(listOfScript);
		for (String stepFileName : lines) {
			OctaSuiteContext.cleanUp();
			File stepFile = new File(stepFileName);
			outputFileName = stepFile.getName().substring(0, stepFile.getName().length()-5).replace("_Script", "_Output")+OctaSuiteContext.getFileSuffix();
			HtmlExtentReporter.createExtentTest(stepFileName);
			try {
				OctaSuiteContext.log("Start of Test Case|"+OctaSuiteContext.getCurrentTime());
				new StepGenerator().startUp(stepFileName);
			}catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage(), e);
				String filePath = OctaSuiteContext.stepScreenshot("errorScreen");
				OctaSuiteContext.log("Test execution|Failed|"+e.getMessage());
				HtmlExtentReporter.logger.fail(e.getMessage(), MediaEntityBuilder.createScreenCaptureFromPath("./"+filePath).build());
				OctaSuiteContext.closeDriver();
			}
			OctaSuiteContext.log("End of Test Case|"+OctaSuiteContext.getCurrentTime());
			HtmlExtentReporter.endReport();
			postSteps(stepFile.getName().substring(0, stepFile.getName().length()-5).replace("_Script", "_Report"));
		}
	}
}