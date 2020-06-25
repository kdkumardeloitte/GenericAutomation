package com.deloitte.rpa.octa.reporter;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.deloitte.rpa.octa.OctaSuiteContext;

public final class HtmlExtentReporter {

	private final static Logger logger4j = Logger.getLogger(HtmlExtentReporter.class);

	public static ExtentSparkReporter htmlReporter;
	public static ExtentReports extent;
	public static ExtentTest logger;

	public static void startReport(String reportName) {
		File stepFile = new File(reportName);
		reportName = stepFile.getName().substring(0, (stepFile.getName().endsWith(".txt")? stepFile.getName().length()-4 : stepFile.getName().length()-5));
		String htmlReportFileName = "./HtmlReport/"+reportName+OctaSuiteContext.getFileSuffix()+".html";
		htmlReporter = new ExtentSparkReporter(htmlReportFileName);

		//Without Time stamp [For Jenkins]
		//htmlReporter = new ExtentSparkReporter("./HtmlReport/"+reportName+".html");

		logger4j.info("HTML Report: "+htmlReportFileName);

		// Create an object of Extent Reports
		extent = new ExtentReports();  
		extent.attachReporter(htmlReporter);
		htmlReporter.config().setDocumentTitle("Automation Result"); 
		// Name of the report
		htmlReporter.config().setReportName(reportName); 
		// Dark Theme
		htmlReporter.config().setTheme(Theme.STANDARD); 
	}

	public static void createExtentTest(String reportName) {
		logger = HtmlExtentReporter.extent.createTest(reportName);
	}

	public static void logError(String filePath) throws IOException {
		logger.fail("Test Step Failed Snapshot is below " + logger.addScreenCaptureFromPath(filePath));
	}

	public static void endReport() {
		extent.flush();
	}
}