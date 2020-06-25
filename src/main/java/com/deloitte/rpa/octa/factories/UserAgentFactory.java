package com.deloitte.rpa.octa.factories;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.deloitte.rpa.octa.constants.UserAgentConstants;

public class UserAgentFactory implements UserAgentConstants {
	private static WebDriver driver;

	public static WebDriver initAgent(String agentName) throws IOException {
		if (agentName.equalsIgnoreCase(FIREFOX)) {
			System.setProperty("webdriver.gecko.driver", "resources/drivers/geckodriver.exe");
			driver = new FirefoxDriver();
		} else if (agentName.equalsIgnoreCase(CHROME)) {
			Map<String, Object> prefs = new HashMap<String, Object>();
			prefs.put("profile.default_content_settings.popups", 0);
			prefs.put("credentials_enable_service", false);
			prefs.put("profile.password_manager_enabled", false);
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("prefs", prefs);
			System.setProperty("webdriver.chrome.driver",  "resources/drivers/chromedriver.exe");
			driver = new ChromeDriver(options);
		} 
		else if (agentName.equalsIgnoreCase(HEADLESS))
		{
			driver = new HtmlUnitDriver(); 
			System.out.println(driver);
		}
		driver.manage().window().maximize();
		return driver;
	}
}