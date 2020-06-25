package com.deloitte.rpa.octa.dataHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.deloitte.rpa.octa.startUp.StepGenerator;

/**
 * Writes data into output file
 */
public class OutputDataHandler {

	private final static Logger logger = Logger.getLogger(OutputDataHandler.class);

	private static final String LIST_PATH = "resources/outputData/";
	private static File file;

	public static void createOutputFile(String fileName) {
		String filePath = LIST_PATH + fileName + ".dat";
		file = new File(filePath);
		logger.info("Output file " + filePath + " created!");
	}

	public static void saveProperties(Properties p) throws IOException
	{
		if(file==null)
			createOutputFile(StepGenerator.getOutputFileName());
		FileOutputStream fr = new FileOutputStream(file);
		p.store(fr, "Output values");
		fr.close();
		logger.info("Saved to output file.");
	}

	public static void loadProperties(Properties p)throws IOException
	{
		FileInputStream fi=new FileInputStream(file);
		p.load(fi);
		fi.close();
		logger.info("Loaded the output file.");
	}
}