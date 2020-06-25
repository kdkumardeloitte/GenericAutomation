package com.deloitte.rpa.octa;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.deloitte.rpa.octa.factories.UserAgentFactory;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public final class OctaSuiteContext {

	private final static Logger logger = Logger.getLogger(OctaSuiteContext.class);

	private static WebDriver driver;

	private static String reconfilename;

	private static int timeout = 45;

	private static ArrayList<String> errorList = new ArrayList<String>();
	private static ArrayList<String> filePathList = new ArrayList<String>();

	private static Float pixelRatio = 2F;

	public static String getReconfilename() {
		return reconfilename;
	}

	private static void setReconfilename(String reconfilename) {
		OctaSuiteContext.reconfilename = reconfilename;
	}

	private static ArrayList<String> getErrorList() {
		return errorList;
	}

	public static WebDriver getDriver() {
		return driver;
	}

	public static int getTimeout() {
		return timeout;
	}

	public static void log(String errMsg) {
		errorList.add(errMsg);
	}

	public static void init(String browser) throws IOException{
		OctaSuiteContext.driver = UserAgentFactory.initAgent(browser);
		getPixelRatio();
	}

	public static void cleanUp() {
		filePathList.clear();
		errorList.clear();
	}

	public static void closeDriver() {
		try {
			driver.close();
			driver.quit();
		}catch(Exception e) {}
	}

	private static void getPixelRatio() {
		try {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			Double pixelRatiovalue = (Double) executor.executeScript("return window.devicePixelRatio;");
			if (pixelRatiovalue == null)
				pixelRatiovalue = 2.0;
			double pixelDouble = pixelRatiovalue;
			pixelRatio = (float) pixelDouble;
			logger.info("Pixel ratio is: " + pixelRatio);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void writeToExcel(String suiteName, String header1, String header2, String header3)
			throws IOException {
		setReconfilename(suiteName);
		Workbook wb1 = new XSSFWorkbook();
		int index = 0;
		InputStream inputStream = null;
		File directory = new File("Reconciliation-Reports");
		if (!directory.exists()) {
			directory.mkdir();
		}
		FileOutputStream fileOut1 = new FileOutputStream(directory + "/" + suiteName + getFileSuffix() + ".xlsx");

		Sheet sheet = wb1.getSheet(suiteName);
		if (sheet != null) {
			index = wb1.getSheetIndex(sheet);
			wb1.removeSheetAt(index);
		}
		sheet = wb1.createSheet(suiteName);
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue(header1);
		row.createCell(1).setCellValue(header2);
		row.createCell(2).setCellValue(header3);

		ArrayList<String> err = getErrorList();
		int rowCounter = 0;
		for (int i = 0; i < err.size(); i++) {
			Row row1 = sheet.createRow(i + 1);
			String recon[] = ((String) err.get(i)).split("\\|");
			for (int reconcount = 0; reconcount < recon.length; reconcount++)
				row1.createCell(reconcount).setCellValue(recon[reconcount]);
			rowCounter++;
		}

		int i = 0;
		rowCounter = rowCounter + 2;
		while (i < filePathList.size()) {
			CreationHelper helper = wb1.getCreationHelper();
			Drawing<?> drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			inputStream = new FileInputStream(filePathList.get(i));
			byte[] bytes = IOUtils.toByteArray(inputStream);
			int pictureIdx = wb1.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			Cell imageName = sheet.createRow(rowCounter).createCell(0);
			imageName.setCellValue(filePathList.get(i).substring(14));
			rowCounter++;
			anchor.setCol1(0);
			anchor.setRow1(rowCounter);
			anchor.setCol2(8);
			anchor.setRow2(rowCounter + 8);
			drawing.createPicture(anchor, pictureIdx);
			Cell cell = sheet.createRow(rowCounter).createCell(1);

			int widthUnits = 20 * 256;
			sheet.setColumnWidth(1, widthUnits);
			short heightUnits = 60 * 20;
			cell.getRow().setHeight(heightUnits);

			rowCounter = rowCounter + 10;
			i++;
		}

		Cell imageName = sheet.createRow(rowCounter).createCell(0);
		imageName.setCellValue("End Of Script");
		// inputStream.close();
		wb1.write(fileOut1);
		fileOut1.close();
		wb1.close();
		cleanUp();
	}

	public static String entirePageScreenshot(WebDriver driver, String name) throws IOException {
		File directory = new File("resources/img");
		if(!directory.exists()) {
			directory.mkdir();
		} 
		String filePath = "resources/img/" + name + "-" + getDateTimeStamp() + ".png";
		Screenshot screenshot = new AShot()
				.shootingStrategy(ShootingStrategies.viewportPasting(ShootingStrategies.scaling(pixelRatio), 1000))
				.takeScreenshot(driver);

		ImageIO.write(screenshot.getImage(), "PNG", new File(filePath));
		filePathList.add(filePath);
		return filePath;
	}

	public static String takeScreenshotOfPage(WebDriver driver, String name) throws IOException {
		File directory = new File("resources/img");
		if(!directory.exists()) {
			directory.mkdir();
		} 
		String filePath = "resources/img/" + name + "-" + getDateTimeStamp() + ".png";
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		BufferedImage srcImage = ImageIO.read(srcFile);
		ImageIO.write(srcImage, "png", new File(filePath));
		filePathList.add(filePath);
		return filePath;
	}

	public static String getDateTimeStamp() {
		// creates a date time stamp that is Windows OS filename compatible
		return new SimpleDateFormat("MMM dd HH.mm.ss").format(Calendar.getInstance().getTime());
	}

	public static String getDateTimeStamp12Hrs() {
		// creates a date time stamp that is Windows OS filename compatible
		return new SimpleDateFormat("MM/dd/yy hh:mm aa").format(Calendar.getInstance().getTime());
	}

	public static String getFileSuffix() {
		String fileSuffix = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss").format(new Date());
		return fileSuffix;
	}

	public static String getCurrentTime() {
		String fileSuffix = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		return fileSuffix;
	}

	public static String stepScreenshot(String name) {
		String path = "";
		try {
			path = OctaSuiteContext.takeScreenshotOfPage(driver, name);
		}catch(Exception e) {
			OctaSuiteContext.log(name+"|Failed");
		}
		return path;
	}

	public static void deleteScreenshots() {
		String parentDirectory = "resources\\img";
		File parentDir = new File(parentDirectory);

		String[] listOfTextFiles = parentDir.list();

		if (listOfTextFiles!=null && listOfTextFiles.length == 0) {
			logger.info("There are no text files in this direcotry!");
			return;
		}

		File fileToDelete;

		for (String file : listOfTextFiles) {

			//construct the absolute file paths...
			String relativeFilePath = new StringBuffer(parentDirectory).append(File.separator).append(file).toString();

			//open the files using the absolute file path, and then delete them...
			fileToDelete = new File(relativeFilePath);
			fileToDelete.delete();
			//boolean isdeleted = fileToDelete.delete();
			//System.out.println("File : " + relativeFilePath + " was deleted : " + isdeleted);
		}
	}
}