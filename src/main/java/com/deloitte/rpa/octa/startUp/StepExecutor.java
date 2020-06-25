package com.deloitte.rpa.octa.startUp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.deloitte.rpa.octa.OctaSuiteContext;
import com.deloitte.rpa.octa.constants.OperationTypeConstants;
import com.deloitte.rpa.octa.constants.SpecialOperationTypeConstants;
import com.deloitte.rpa.octa.dataHandler.OutputDataHandler;
import com.deloitte.rpa.octa.reporter.HtmlExtentReporter;
import com.deloitte.rpa.octa.security.AESDecryption;
import com.deloitte.rpa.octa.ui.operation.OperationUtility;

public class StepExecutor implements OperationTypeConstants, SpecialOperationTypeConstants {

	private final static Logger logger = Logger.getLogger(StepExecutor.class);

	private static OperationUtility operationUtility;
	private static Workbook stepWorkbook;

	private static WebDriver driver;
	private int index = 0;
	private String rowNumber = null;
	private String getTextValue = null;
	private String getAttributeValue = null;
	private String status = null;
	public static Boolean conditionResult = null; 

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Set<String> getElementKeys(String stepFileName) throws EncryptedDocumentException, InvalidFormatException, IOException {
		Set<String> setOfElementKeys = new LinkedHashSet<>();
		InputStream inputSteam = new FileInputStream(stepFileName);
		stepWorkbook = WorkbookFactory.create(inputSteam);
		DataFormatter df = new DataFormatter();
		Sheet sheet = stepWorkbook.getSheet("Element-Operations");
		int totalRowCount = sheet.getLastRowNum();
		for (int i = 1; i <= totalRowCount; i++) {
			String key = df.formatCellValue(sheet.getRow(i).getCell(5)).trim();
			if(!key.isEmpty()) {
				if(key.contains(",")) {
					String [] keys = key.split(",");
					for (String value : keys) {
						setOfElementKeys.add(value);
					}
				}else
					setOfElementKeys.add(key);
			}
		}
		return setOfElementKeys;
	}

	public Map<String, String> getRequiredXpathMap(String xpathFileName, Set<String> setOfElementKeys) throws EncryptedDocumentException, InvalidFormatException, IOException{
		Map<String, String> xpathMap = new LinkedHashMap<>();
		InputStream inputSteam = new FileInputStream(xpathFileName);
		Workbook xpathWorkbook = WorkbookFactory.create(inputSteam);
		DataFormatter df = new DataFormatter();
		Sheet sheet = xpathWorkbook.getSheet("Element-Xpath Details");
		int totalRowCount = sheet.getLastRowNum();
		for (int i = 1; i <= totalRowCount; i++) {
			String elementName = df.formatCellValue(sheet.getRow(i).getCell(0)).trim();
			if(elementName!=null && !elementName.isEmpty()) {
				if(setOfElementKeys.contains(elementName))
					xpathMap.put(elementName, df.formatCellValue(sheet.getRow(i).getCell(1)).trim());
			}
		}
		return xpathMap;
	}

	public String getXpathPostSpecialOperation(String specialOperation, String elementXpath, String value) {
		if(specialOperation.equalsIgnoreCase(REPLACE_WITH_VALUE)) {
			elementXpath = elementXpath.replaceAll(REPLACE_WITH_VALUE, value);
		}else if(specialOperation.equalsIgnoreCase(REPLACE_WITH_LINE_INDEX)) {
			elementXpath = elementXpath.replaceAll(REPLACE_WITH_LINE_INDEX, Integer.toString(index));
		}else if(specialOperation.equalsIgnoreCase(REPLACE_WITH_ROW_NUMBER)) {
			elementXpath = elementXpath.replaceAll(REPLACE_WITH_ROW_NUMBER, rowNumber);
		}else if(specialOperation.equalsIgnoreCase(REPLACE_WITH_GET_TEXT_VALUE)) {
			elementXpath = elementXpath.replaceAll(REPLACE_WITH_GET_TEXT_VALUE, getTextValue);
		}else if(specialOperation.equalsIgnoreCase(REPLACE_WITH_GET_ATTRIBUTE_VALUE)) {
			elementXpath = elementXpath.replaceAll(REPLACE_WITH_GET_ATTRIBUTE_VALUE, getAttributeValue);
		}
		return elementXpath;
	}


	public void stepExecutor(Map<String, String> xpathMap, String specialOperation, String operationType, String directValue, 
			String fileValue, String xpathKey, String timeOut, String stepDescription) throws Exception {

		String elementXpath = xpathMap.get(xpathKey);
		String value = directValue;

		if(directValue==null || directValue.isEmpty())
			value = fileValue;
		int sleepTime = 0;
		if(timeOut!=null && !timeOut.isEmpty()) {
			sleepTime = Integer.valueOf(timeOut);
		}

		if(specialOperation!=null && !specialOperation.isEmpty() && elementXpath!=null) {
			if(specialOperation.contains(",")) {
				String [] specialOperations = specialOperation.split(",");
				for (String so : specialOperations) {
					if(so.startsWith("replace"))
						elementXpath = getXpathPostSpecialOperation(so, elementXpath, value);
				}
				for (String so : specialOperations) {
					if(so.equalsIgnoreCase(CONDITION_IF_EXIST)) {
						if(!operationUtility.whetherXpathExist(elementXpath, 10))
							return;
					}
				}
			}
			else if(specialOperation.equalsIgnoreCase(CONDITION_IF_EXIST)) {
				if(!operationUtility.whetherXpathExist(elementXpath, 10))
					return;
			}else {
				elementXpath = getXpathPostSpecialOperation(specialOperation, elementXpath, value);
			}
			if(specialOperation.equalsIgnoreCase(USE_TEXT_STRING))
				value = getTextValue;
			else if(specialOperation.equalsIgnoreCase(USE_ATTRIBUTE_STRING))
				value = getAttributeValue;
			if(specialOperation.contains(DECRYPT)) {
				value = AESDecryption.getDecryptedValue(value);
			}
		}
		try {
			switch (operationType) {
			case LAUNCH_APPLICATION:
				OctaSuiteContext.init(value);
				operationUtility = new OperationUtility();
				driver = OctaSuiteContext.getDriver();
				Thread.sleep(1000*sleepTime);
				logger.info("Launched Browser: "+value);
				break;
			case CLOSE_BROWSER:
				OctaSuiteContext.closeDriver();
				logger.info("Driver closed.");
				break;
			case BROWSER_NAVIGATION:
				operationUtility.getUrl(value);
				Thread.sleep(1000*sleepTime);
				logger.info("Redirected to URL: "+value);
				break;
			case SEND_KEY:
				if(value!=null && !value.isEmpty())
					operationUtility.inputElement(elementXpath, value);
				Thread.sleep(1000*sleepTime);
				logger.info("Enetered Value: "+value);
				break;
			case SEND_KEYBOARD_KEY:
				operationUtility.inputKeyboardKey(elementXpath, value);
				Thread.sleep(1000*sleepTime);
				logger.info("Enetered Keyboard Value: "+value);
				break;
			case CLEAR_FIELD:
				operationUtility.clearField(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Field cleared!!");
				break;
			case CLICK_ELEMENT:
				operationUtility.clickElement(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Clicked element: "+elementXpath);
				break;
			case JAVASCRIPT_CLICK_ELEMENT:
				operationUtility.javascriptClick(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Javascript Clicked element: "+elementXpath);
				break;
			case VERIFY_PAGE_TITLE:
				operationUtility.waitForTitle(value);
				Thread.sleep(1000*sleepTime);
				logger.info("Waited For Title: "+value);
				break;
			case WAIT_FOR_ELEMENT_TO_BE_VISIBLE:
				operationUtility.waitUntilXpathVisible(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Waited For Element: "+elementXpath);
				break;
			case TAKE_SCREENSHOT:
				OctaSuiteContext.takeScreenshotOfPage(driver, value);
				Thread.sleep(1000*sleepTime);
				logger.info("Screenshot Taken!");
				break;
			case ENTIRE_PAGE_SCREENSHOT:
				OctaSuiteContext.entirePageScreenshot(driver, value);
				Thread.sleep(1000*sleepTime);
				logger.info("Entire page Screenshot Taken!");
				break;
			case SELECT_ELEMENT:
				operationUtility.selectElement(elementXpath, value);
				Thread.sleep(1000*sleepTime);
				logger.info("Element "+value+" Selected!");
				break;
			case SELECT_SUGGESSION:
				if(value!=null && !value.isEmpty())
					operationUtility.selectSuggession(elementXpath, value);
				Thread.sleep(1000*sleepTime);
				logger.info("Element "+value+" Selected!");
				break;
			case HAS_VALUE:
				operationUtility.hasValue(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Element has value!");
				break;
			case SEARCH_AND_SELECT:
				if(value!=null && !value.isEmpty())
					operationUtility.searchAndSelect(elementXpath, value);
				Thread.sleep(1000*sleepTime);
				logger.info("Searched and selectedc element value!");
				break;
			case SCROLL_TO_VIEW:
				operationUtility.scrollToView(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Scrolled in view...");
				break;
			case WAIT_FOR_ELEMENT_TO_BE_INVISIBLE:
				operationUtility.waitUntilXpathInvisible(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Waited for element {"+elementXpath+"} to be invisible.");
				break;
			case GET_DYNAMIC_ROW_NUMBER:
				rowNumber = null;
				String [] groupAndPattern = value.split(";;");
				String pattern = null;
				String group = null;
				for (String arrayValue : groupAndPattern) {
					if(arrayValue.trim().toLowerCase().startsWith("pattern-"))
						pattern = arrayValue.trim().substring(8);
					if(arrayValue.trim().toLowerCase().startsWith("group-"))
						group = arrayValue.trim().substring(6);
				}
				logger.info("Pattern: "+pattern);
				logger.info("Group: "+group);
				if(pattern==null || group==null)
					throw new Exception("Invalid parameters for this Operation "+operationType);
				rowNumber = operationUtility.rowNumber(elementXpath, pattern, group);
				if(rowNumber==null)
					throw new Exception("Dynamic row number not found !");
				Thread.sleep(1000*sleepTime);
				logger.info("Dyanmic row number found: "+rowNumber);
				break;
			case DOUBLE_CLICK_ELEMENT:
				operationUtility.doubleClick(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Double Clicked element: "+elementXpath);
				break;
			case SWITCH_TO_FRAME:
				operationUtility.switchToFrame(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Switched to frame: "+elementXpath);
				break;
			case SWITCH_WINDOW:
				operationUtility.switchWindow();
				Thread.sleep(1000*sleepTime);
				logger.info("Switched to window.");
				break;
			case SWITCH_TO_DEFAULT_CONTENT:
				operationUtility.switchToDefaultContent();
				Thread.sleep(1000*sleepTime);
				logger.info("Switch to default content.");
			case CLOSE_CHILD_WINDOW:
				operationUtility.closeChildWindow();
				Thread.sleep(1000*sleepTime);
				logger.info("Closed child window.");
				break;
			case EXPLICIT_WAIT:
				operationUtility.explicitWait(value);
				Thread.sleep(1000*sleepTime);
				logger.info("Explicit wait is set.");
				break;
			case DEFAULT_WAIT:
				operationUtility.defaultWait();
				Thread.sleep(1000*sleepTime);
				logger.info("Wait is set to default value.");
				break;
			case GET_TEXT:
				getTextValue = operationUtility.getText(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Text Obtained: "+getTextValue);
				break;
			case GET_ATTRIBUTE:
				getAttributeValue = operationUtility.getAttribute(elementXpath, value);
				Thread.sleep(1000*sleepTime);
				logger.info("Text Obtained: "+getAttributeValue);
				break;
			case GET_SUBSRTING:
				String inputString;
				if(value!=null) {
					if(specialOperation.equalsIgnoreCase(USE_TEXT_STRING))
						inputString = getTextValue;
					else if(specialOperation.equalsIgnoreCase(USE_ATTRIBUTE_STRING))
						inputString = getAttributeValue;
					else
						throw new Exception("Invalid Syntax for operation type: "+GET_SUBSRTING);
				}else
					throw new Exception("Invalid Syntax for operation type: "+GET_SUBSRTING);
				groupAndPattern = value.split(";;");
				pattern = null;
				group = null;
				for (String arrayValue : groupAndPattern) {
					if(arrayValue.trim().toLowerCase().startsWith("pattern-"))
						pattern = arrayValue.trim().substring(8);
					if(arrayValue.trim().toLowerCase().startsWith("group-"))
						group = arrayValue.trim().substring(6);
				}
				String matchedValue = operationUtility.getSubString(inputString, pattern, group);
				if(specialOperation.equalsIgnoreCase(USE_TEXT_STRING))
					getTextValue = matchedValue;
				else if(specialOperation.equalsIgnoreCase(USE_ATTRIBUTE_STRING))
					getAttributeValue = matchedValue;
				logger.info("SubString: "+matchedValue);
				break;
			case GET_PROCESS_STATUS:
				if(xpathKey.contains(",")) {
					String[] xpathKeys = xpathKey.split(",");
					String refreshXpath = xpathMap.get(xpathKeys[0]);
					String statusXpath = xpathMap.get(xpathKeys[1]);
					status = operationUtility.monitorProcess(
							getXpathPostSpecialOperation(specialOperation, refreshXpath, value), 
							getXpathPostSpecialOperation(specialOperation, statusXpath, value), 
							value);
					logger.info("Process finished with status: "+status);
				}else {
					throw new Exception("Sufficient parameters are not passed");
				}
				break;
			case GET_PROGRESS:
				status = operationUtility.getProgress(elementXpath, value);
				logger.info("Progress: "+status);
				break;
			case ZOOM_OUT:
				operationUtility.zoomOut(Integer.valueOf(value));
				Thread.sleep(1000*sleepTime);
				logger.info("Zoomed out times: "+value);
				break;
			case ZOOM_IN:
				operationUtility.zoomIn(Integer.valueOf(value));
				Thread.sleep(1000*sleepTime);
				logger.info("Zoomed in times: "+value);
				break;
			case WRITE_TO_OUTPUT_FILE:
				value = fileValue;
				if(specialOperation.equalsIgnoreCase(USE_TEXT_STRING))
					value = getTextValue;
				else if(specialOperation.equalsIgnoreCase(USE_ATTRIBUTE_STRING))
					value = getAttributeValue;
				StepGenerator.outputValues.setProperty(directValue, value);
				OutputDataHandler.saveProperties(StepGenerator.outputValues);
				OutputDataHandler.loadProperties(StepGenerator.outputValues);
				logger.info("Written into output file: "+value);
				break;
			case COMMENT:
				OctaSuiteContext.log("Commemt|"+value);
				break;
			case CONTROL_ALL:
				operationUtility.controlAll(elementXpath);
				Thread.sleep(1000*sleepTime);
				logger.info("Control All is done: ");
				break;
			case IF_XPATH_EXIST:
				conditionResult = operationUtility.whetherXpathExist(elementXpath, 8);
				logger.info("Result of condition "+IF_XPATH_EXIST+" :"+conditionResult);
				break;
			case IF_IS_SELECTED:
				conditionResult = operationUtility.isSelected(elementXpath);
				logger.info("Result of condition "+IF_IS_SELECTED+" :"+conditionResult);
				break;
			case IF_IS_ENABLED:
				conditionResult = operationUtility.isEnabled(elementXpath);
				logger.info("Result of condition "+IF_IS_ENABLED+" :"+conditionResult);
				break;
			case IF_IS_DISPLAYED:
				conditionResult = operationUtility.isDisplyed(elementXpath);
				logger.info("Result of condition "+IF_IS_DISPLAYED+" :"+conditionResult);
				break;
			case IF_DATA_MATCH:
				value = fileValue;
				if(specialOperation.equalsIgnoreCase(USE_TEXT_STRING))
					value = getTextValue;
				else if(specialOperation.equalsIgnoreCase(USE_ATTRIBUTE_STRING))
					value = getAttributeValue;
				conditionResult = operationUtility.dataMatch(directValue, value);
				logger.info("Result of condition "+IF_DATA_MATCH+" :"+conditionResult);
				break;
			default:
				logger.info("Step "+operationType+" not found!!! ");
				break;
			}
		}catch(Exception e) {
			if(stepDescription!=null && !stepDescription.isEmpty()) 
				HtmlExtentReporter.logger.log(Status.FAIL, MarkupHelper.createLabel(stepDescription, ExtentColor.RED));
			else
				HtmlExtentReporter.logger.log(Status.FAIL, MarkupHelper.createLabel(operationType, ExtentColor.RED));
			throw e;
		}

		if(stepDescription!=null && !stepDescription.isEmpty()) {
			if(stepDescription.toLowerCase().endsWith("|failed")) {
				//OctaSuiteContext.log(stepDescription);
				stepDescription = stepDescription.substring(0, stepDescription.length()-7);
				//HtmlExtentReporter.logger.log(Status.FAIL, MarkupHelper.createLabel(stepDescription, ExtentColor.RED));
				throw new Exception(stepDescription);
			}
			String finalDescription = stepDescription+"|Successful";
			if(specialOperation.contains(ADD_VALE_TO_STEP_DESCRIPTION))
				finalDescription = finalDescription+"|"+value;
			OctaSuiteContext.log(finalDescription);
			HtmlExtentReporter.logger.log(Status.PASS, MarkupHelper.createLabel(stepDescription, ExtentColor.GREEN));
			//OctaSuiteContext.stepScreenshot(stepDescription.replace(" ", "_"));
		}
	}
}