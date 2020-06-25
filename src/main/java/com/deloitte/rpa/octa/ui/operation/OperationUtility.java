package com.deloitte.rpa.octa.ui.operation;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import com.deloitte.rpa.octa.OctaSuiteContext;
import com.deloitte.rpa.octa.factories.KeyboardKeys;

public class OperationUtility {

	private final static Logger logger = Logger.getLogger(OperationUtility.class);

	private WebDriver driver;
	private Wait<WebDriver> wait;

	public OperationUtility() {
		this.driver = OctaSuiteContext.getDriver();
		this.wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(OctaSuiteContext.getTimeout()))
				.ignoring(NoSuchElementException.class);
	}

	////////////////////////////////////////

	/**
	 * General Functions
	 */

	/**
	 * Sets the explicit time out
	 * @param seconds
	 */
	public void explicitWait(String seconds) {
		this.wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(Integer.valueOf(seconds)))
				.ignoring(NoSuchElementException.class);
	}

	/**
	 * Sets wait object to default time out
	 */
	public void defaultWait() {
		this.wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(OctaSuiteContext.getTimeout()))
				.ignoring(NoSuchElementException.class);
	}

	/**
	 * Navigate to the given URL
	 * @param url
	 */
	public void getUrl(String url) {
		driver.get(url);
	}

	/**
	 * Send Keys to a text field
	 * @param xpath
	 * @param data
	 */
	public void inputElement(String xpath, String data)
	{
		logger.info("Adding Input: "+data+" In "+xpath);
		By xPath = By.xpath(xpath);	
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		wait.until(ExpectedConditions.elementToBeClickable(xPath));
		driver.findElement(xPath).clear();
		driver.findElement(xPath).sendKeys(data);	
	}

	public void clearField(String xpath)
	{
		logger.info("Clearing field : "+xpath);
		By xPath = By.xpath(xpath);	
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		wait.until(ExpectedConditions.elementToBeClickable(xPath));
		driver.findElement(xPath).clear();
	}

	/**
	 * Perform keyboard operations
	 * @param xpath
	 * @param key
	 */
	public void inputKeyboardKey(String xpath, String key) {
		By xPath = By.xpath(xpath);	
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		wait.until(ExpectedConditions.elementToBeClickable(xPath));
		driver.findElement(xPath).sendKeys(KeyboardKeys.getKeyboardOperation(key));
	}

	/**
	 * Click on element
	 * @param xpath
	 */
	public void clickElement(String xpath)
	{
		logger.info("Before Clicking on Xpath "+xpath);
		By xPath = By.xpath(xpath);
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		wait.until(ExpectedConditions.elementToBeClickable(xPath)); 
		try {
			driver.findElement(xPath).click();
		}catch(Exception e) {
			Actions action = new Actions(driver);
			action.moveToElement(driver.findElement(xPath)).click().perform();
		}
	}

	/**
	 * Use JavaScript to click on element
	 * @param xpath
	 */
	public void javascriptClick(String xpath) {
		logger.info("Before javascript clicking on Xpath "+xpath);
		By xPath = By.xpath(xpath);
		wait.until(ExpectedConditions.presenceOfElementLocated(xPath));
		((JavascriptExecutor)driver).executeScript("arguments[0].click();", driver.findElement(xPath));
	}

	/**
	 * Wait for the page title to be same as given title
	 * @param title
	 */
	public void waitForTitle(String title) {
		wait.until(ExpectedConditions.titleContains(title));
	}

	/**
	 * Wait for the xpath to become visible
	 * @param xpath
	 */
	public void waitUntilXpathVisible(String xpath)
	{
		logger.info("waitfor: "+xpath);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		logger.info("waitover: "+xpath);
	}

	/**
	 * Select the value for the HTML <select> element
	 * @param xpath
	 * @param data
	 */
	public void selectElement(String xpath,String data)
	{
		logger.info("Before Selecting on Xpath "+xpath);
		By selAction = By.xpath(xpath);
		wait.until(ExpectedConditions.visibilityOfElementLocated(selAction));
		WebElement actionElement = driver.findElement(selAction);
		Select dropdown= new Select(actionElement);
		dropdown.selectByVisibleText(data);
	}

	/**
	 * Check if the field has value
	 * @param xpath
	 */
	public void hasValue(String xpath) {
		By byElement = By.xpath(xpath);
		while(true) {
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(byElement));
				wait.until(ExpectedConditions.attributeToBeNotEmpty(driver.findElement(byElement), "value"));
				break;
			}catch(StaleElementReferenceException e) {
				//wait.until(ExpectedConditions.attributeToBeNotEmpty(driver.findElement(byElement), "value"));
			}
		}
	}

	/**
	 * Scroll element into view
	 * @param xpath
	 */
	public void scrollToView(String xpath) {
		By byElement = By.xpath(xpath);
		wait.until(ExpectedConditions.presenceOfElementLocated(byElement));
		WebElement webElement = driver.findElement(byElement);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", webElement);
	}

	/**
	 * Wait for the xpath to become invisible
	 * @param xpath
	 */
	public void waitUntilXpathInvisible(String xpath)
	{
		logger.info("waitfor: "+xpath);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
		logger.info("waitover: "+xpath);
	}

	/**
	 * Check if xpath exists
	 * @param xpath
	 * @param seconds
	 * @return
	 * @throws InterruptedException
	 */
	public boolean whetherXpathExist(String xpath,int seconds) throws InterruptedException
	{
		logger.info("waiting for xpath: "+xpath);
		int i=0;
		while(i++<seconds)
		{
			try {
				driver.findElement(By.xpath(xpath));
				logger.info("xpath found");
				return true;
			}catch(NoSuchElementException e) {
				logger.info("xpath notfound:"+i);
				Thread.sleep(1000);
			}
		}
		return false;
	}

	/**
	 * Switch to a frame/iframe
	 * @param xpath
	 */
	public void switchToFrame(String xpath) {
		By byElement = By.xpath(xpath);
		wait.until(ExpectedConditions.visibilityOfElementLocated(byElement));
		driver.switchTo().frame(driver.findElement(byElement));
	}

	/**
	 * Switch to default content
	 */
	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
	}

	/**
	 * Switch to new window/tab
	 * @throws InterruptedException
	 */
	public void switchWindow() throws InterruptedException {
		int tries = 0;
		while( driver.getWindowHandles().size()<2 && tries<10) {
			Thread.sleep(2000);
			tries++;
		}
		for (String childWindow : driver.getWindowHandles())
		{
			driver.switchTo().window(childWindow);
			Thread.sleep(5000);
		}
	}

	/**
	 * Browser zoom out
	 * @param times
	 * @throws AWTException
	 */
	public void zoomOut(int times) throws AWTException {
		for(int i=0; i<times; i++){
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_MINUS);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_MINUS);
		}
	}

	/**
	 * Browser zoom in
	 * @param times
	 * @throws AWTException
	 */
	public void zoomIn(int times) throws AWTException {
		for(int i=0; i<times; i++){
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ADD);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_ADD);
		}
	}

	/**
	 * Close child window/tab
	 * @throws InterruptedException
	 */
	public void closeChildWindow() throws InterruptedException {
		((JavascriptExecutor) driver).executeScript("window.close();");
		for (String parentWindow : driver.getWindowHandles())
		{
			driver.switchTo().window(parentWindow);
			Thread.sleep(5000);
		}
		driver.switchTo().defaultContent();
	}

	/**
	 * Double click on element
	 * @param xpath
	 */
	public void doubleClick(String xpath) {
		By xPath = By.xpath(xpath);
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		wait.until(ExpectedConditions.elementToBeClickable(xPath)); 
		Actions action = new Actions(driver);
		action.moveToElement(driver.findElement(xPath)).doubleClick().perform();
	}

	/**
	 * Get text from element
	 * @param xpath
	 * @return
	 */
	public String getText(String xpath)
	{
		logger.info("Getting text from "+xpath);
		By xPath = By.xpath(xpath);
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		wait.until(ExpectedConditions.elementToBeClickable(xPath)); 
		String text=driver.findElement(xPath).getText();
		return text;
	}

	/**
	 * Get text from an attribute in element
	 * @param xpath
	 * @param attribute
	 * @return
	 */
	public String getAttribute(String xpath, String attribute)
	{
		logger.info("Getting attribute from "+xpath);
		By xPath = By.xpath(xpath);
		//wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		//wait.until(ExpectedConditions.elementToBeClickable(xPath)); 
		wait.until(ExpectedConditions.presenceOfElementLocated(xPath));
		String text = driver.findElement(xPath).getAttribute(attribute);
		return text;
	}

	public void controlAll(String xpath) {
		By xPath = By.xpath(xpath);
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		wait.until(ExpectedConditions.elementToBeClickable(xPath)); 
		String select = Keys.chord(Keys.CONTROL, "a");
		driver.findElement(xPath).sendKeys(select);
	} 

	public String getSubString(String inputString, String pattern, String group) {
		String matchedValue = null;
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(inputString);
		if (m.find( )) {
			matchedValue = m.group(Integer.valueOf(group));
		}
		return matchedValue;
	}

	public boolean dataMatch(String actualData, String requiredData) {
		boolean result = false;
		if(actualData!=null && requiredData!=null)
			result = actualData.equals(requiredData);
		return result;
	}

	public boolean isDisplyed(String xpath) throws InterruptedException {
		By xPath = By.xpath(xpath);
		if(whetherXpathExist( xpath, 1))
			return driver.findElement(xPath).isDisplayed();
		return false;
	}

	public boolean isSelected(String xpath) {
		By xPath = By.xpath(xpath);
		return driver.findElement(xPath).isSelected();
	}

	public boolean isEnabled(String xpath) {
		By xPath = By.xpath(xpath);
		return driver.findElement(xPath).isEnabled();
	}


	/**
	 * Special Functions for Oracle Applications
	 */
	public String rowNumber(String xpath, String pattern, String group) throws InterruptedException {
		String value = null;
		int count = 0;
		By byElement = By.xpath(xpath);
		List<WebElement> webElements = driver.findElements(byElement);
		while((webElements.isEmpty() || value==null) && count<5){
			webElements = driver.findElements(byElement);
			if(!webElements.isEmpty()) {
				WebElement webElement = webElements.get(0);
				logger.info("WebElement : "+webElement);
				logger.info("Value Attribute|"+webElement.getAttribute("value")+"|");
				if(webElement.getAttribute("disabled")==null && (webElement.getAttribute("value")==null || webElement.getAttribute("value").isEmpty())) {
					String id = webElement.getAttribute("id");
					logger.info("id of web element: "+id);
					//String pattern = "(.*)([0-9]+)(:.*::content)$";
					Pattern r = Pattern.compile(pattern);
					Matcher m = r.matcher(id);
					if (m.find( )) {
						logger.info("matcher: "+m.groupCount());
						value = m.group(Integer.valueOf(group));
						//value = m.group(2);
					}
				}
			}
			Thread.sleep(2000);
			count++;
			logger.info("Webelement size: "+webElements.size());
			logger.info("rowNumber() Count: "+count);
		}

		return value;
	}

	public void searchAndSelect(String xpath, String data) throws InterruptedException
	{
		logger.info("Search and Select Input "+data);
		//clickElement(xpath);
		logger.info("Adding Input:"+data+" In "+xpath);
		By xPath = By.xpath(xpath);		
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		driver.findElement(xPath).clear();
		driver.findElement(xPath).sendKeys(data,Keys.TAB);	
		if(!whetherXpathExist(".//input[contains(@id,'afrLovInternalQueryId:value00::content')]", 10))
		{

			return;
		}

		if(!whetherXpathExist(".//*[contains(@id,'afrLovInternalTableId::db')]/table/tbody/tr[1]/td[1]", 10))
		{
			inputElement(".//input[contains(@id,'afrLovInternalQueryId:value00::content')]", data);
			clickElement(".//button[contains(@id,'afrLovInternalQueryId::search')]");
			//return;
		}
		//clickElement(".//a[text()='Search...'  and contains(@id,'dropdownPopup::popupsearch')]");
		try {

			try {
				clickElement("(.//*[contains(@id,'afrLovInternalTableId::db')]/table//following::*[text()='"+data+"'])[1]");				
			}catch(Exception e) {
				clickElement(".//*[contains(@id,'afrLovInternalTableId::db')]/table/tbody/tr[1]/td[1]");
			}
			Thread.sleep(500);
			clickElement(".//button[contains(@id,'lovDialogId::ok')]");
			waitUntilXpathInvisible(".//button[contains(@id,'lovDialogId::ok')]");

		}catch(Exception e)
		{
			clickElement(".//button[contains(@id,'lovDialogId::cancel')]");

		}
	}

	public void selectSuggession(String xpath, String text) throws InterruptedException {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		driver.findElement(By.xpath(xpath)).clear();
		driver.findElement(By.xpath(xpath)).sendKeys(text);

		StringBuilder xpathBuilder = new StringBuilder();
		if(text.contains(" ")) {
			String [] partOfText = text.split(" ");
			xpathBuilder.append("//span[text()=\""+partOfText[0]+"\"]");
			for (int i = 1; i < partOfText.length; i++) {
				xpathBuilder.append("//following::span[text()=\""+partOfText[i]+"\"]");
			}
		}else
			xpathBuilder.append("//*[contains(text(),\"" + text + "\")]");

		By searchResult = By.xpath(xpathBuilder.toString());
		wait.until(ExpectedConditions.visibilityOfElementLocated(searchResult));
		driver.findElement(searchResult).click();
	} 

	public void findElementWithAjax(String scrollBarXpath, String elementXpath) throws Exception{
		int flag = 0;
		int count = 0;

		do{	
			//element to search for while scrolling in grid
			try{
				//element to search for while scrolling in grid
				driver.findElement(By.xpath(elementXpath));
				scrollToView(elementXpath);
				flag=1;
			} catch(Throwable e){
				//scrolling the grid using the grid's xpath
				driver.findElement(By.xpath(scrollBarXpath)).sendKeys(Keys.PAGE_DOWN);
				logger.info("Moving Down...");
				Thread.sleep(2000);
			}

		}while((flag==0) || ((++count)==200));

		if(flag==1){
			logger.info("Element has been found!!");
		}else{
			logger.info("Element has not been found.");
			throw new Exception("Element: "+elementXpath+" not found!");
		}
	}


	public String monitorProcess(String refreshIconXpath, String statusOfProcessIdXpath, String requiredValue) throws InterruptedException {
		String status = null;
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(refreshIconXpath)));
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(refreshIconXpath)));

		while(true) {
			try {
				List<WebElement> statusElements = driver.findElements(By.xpath(statusOfProcessIdXpath));
				if(statusElements.size()>0) {
					WebElement statusElement = statusElements.get(0);
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", statusElement);
					Thread.sleep(500); 
					status = statusElement.getText();
					if(status!=null) {
						if(requiredValue.contains(status)) {
							break;
						}
					}
				}
				executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(refreshIconXpath)));
			}catch(StaleElementReferenceException e) {
			}
			Thread.sleep(2000);
		}
		return status;
	}

	public String getProgress(String elementXpath, String value) throws InterruptedException {
		String status = driver.findElement(By.xpath(elementXpath)).getText();
		while(status==null || status.isEmpty() || !value.contains(status))
		{
			Thread.sleep(3000);
			status=driver.findElement(By.xpath(elementXpath)).getText();
			logger.info("Current Status: "+status);
		}
		return status;
	}

	////////////////////////////////////////

	@SuppressWarnings("deprecation")
	public String dateConverter(String sdate) throws ParseException
	{
		Date date = new Date(sdate);

		DateFormat destDf = new SimpleDateFormat("M/D/YY");

		// format the date into another format
		String dateStr = destDf.format(date);
		System.out.println("Provided date is:"+date+"Converted date is : " + dateStr);
		dateStr=sdate;

		return dateStr;
	}

	@SuppressWarnings("deprecation")
	public String dateConverter(String sdate,String format) throws ParseException
	{
		System.out.println("Provide date is2:"+sdate);
		Date date = new Date(sdate);
		System.out.println("Provide date is:"+date);

		DateFormat destDf = new SimpleDateFormat(format);

		// format the date into another format
		String dateStr = destDf.format(date);

		System.out.println("Provide date is:"+date+"Converted date is : " + dateStr);
		dateStr=sdate;
		return dateStr;
	}

	public boolean waitforProcessCompletion(String xpath,int seconds) throws InterruptedException
	{
		System.out.println("waiting for Process Completion");
		boolean completed = false;

		int counter=0;

		while(!completed && counter<10){
			// driver.findElement(By.xpath("//div[@title='Refresh' and contains(@id,':refresh')]")).click();
			clickElement("//div[@title='Refresh' and contains(@id,':refresh')]");
			Thread.sleep(10000);
			completed=driver.findElement(By.xpath("//*[@title='Completed']")).isDisplayed();
			System.out.println(completed);
			counter++;
			if(!completed && counter==10){
				return false;
			}
		}
		return false;

	}

	public void waitForAlert(WebDriver driver) throws InterruptedException
	{
		int i=0;
		while(i++<25)
		{
			try
			{
				int size = driver.findElements(By.tagName("iframe")).size();
				System.out.println("Number of frames "+size);                                      
				driver.switchTo().frame("wfx-frame-smartPopup");

				WebElement okbtn = driver.findElement(By.xpath("//button[text()='OK']"));
				System.out.println("Ok Button Found");  
				okbtn.click();

				break;
			}
			catch(Exception e)
			{
				Thread.sleep(1000);
				// continue;
			}
		}
	}

	//Getting the system date with time stamp
	public String getStringDate() {
		DateFormat df = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
		System.out.println("Date -> " +df.format(new Date()));
		return df.format(new Date());
	}

	public void selectElementInput(String xpath,String data) throws InterruptedException
	{
		//give xpath without content in it.
		System.out.println("Before Selecting on Xpath "+xpath+" data:"+data);
		clickElement(xpath);
		xpath=xpath.replaceFirst("input", "ul");
		xpath=xpath.replaceFirst("content", "pop");
		clickElement(xpath+"/li[text()='"+data+"']");

	}

	public void SearchAndSelectFull(String xpath, String data) throws InterruptedException
	{

		clickElement(xpath);
		try {
			clickElement(".//a[text()='Search...']");

			inputElement(".//input[contains(@id,'afrLovInternalQueryId:value00::content')]",data);
			clickElement(".//button[contains(@id,'_afrLovInternalQueryId::search')]");

			clickElement(".//*[contains(@id,'_afrLovInternalTableId::db')]/table/tbody/tr/td[1]");

			Thread.sleep(500);
			clickElement(".//button[contains(@id,'lovDialogId::ok')]");
			waitUntilXpathInvisible(".//button[contains(@id,'lovDialogId::ok')]");

		}catch(Exception e)
		{
			clickElement(".//button[contains(@id,'lovDialogId::cancel')]");

		}
	}

	public void inputElement(String xpath, String data,Keys Key)
	{
		System.out.println("Adding Input:"+data+"In "+xpath);
		By xPath = By.xpath(xpath);	
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		wait.until(ExpectedConditions.elementToBeClickable(xPath));
		driver.findElement(By.xpath(xpath)).clear();
		driver.findElement(xPath).sendKeys(data,Key);	
	}

	public void SearchAndSelectEnter(String xpath, String text) throws InterruptedException
	{
		/*System.out.println("Search and Select Input "+data);
		//clickElement(xpath);
		System.out.println("Adding Input:"+data+"In "+xpath);
		By xPath = By.xpath(xpath);		
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		driver.findElement(xPath).clear();
		driver.findElement(xPath).sendKeys(data,Keys.ENTER);	
		//clickElement(".//a[text()='Search...'  and contains(@id,'dropdownPopup::popupsearch')]");
		try {

		}catch(Exception e)
		{
			clickElement(".//button[contains(@id,'lovDialogId::cancel')]");
		}*/
		String _POPUP_PANEL_ID = "_afrautosuggestpopup";
		// final By moreInfoAnchor = By.xpath(".//a[@title='More...']");
		//By moreInfoAnchor = By.xpath("..//a");
		int i=0;
		WebElement element=driver.findElement(By.xpath(xpath));
		wait.until(ExpectedConditions.elementToBeClickable(element));
		while (true) {
			try {
				Thread.sleep(500);
				element.clear();
				element.sendKeys(text);
				By firstResult = By.xpath(".//*[contains(@id,'" + _POPUP_PANEL_ID + "')]/li[1]");
				wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(firstResult)));
				wait.until(ExpectedConditions.visibilityOfElementLocated(firstResult));
				wait.until(ExpectedConditions.elementToBeClickable(firstResult));
				System.out.print("Javascript Executor");
				((JavascriptExecutor) driver).executeScript("arguments[0].click();",
						driver.findElement(By.xpath(".//*[contains(@id,'" + _POPUP_PANEL_ID + "')]/li[1]")));
				wait.until(ExpectedConditions.attributeToBeNotEmpty(element, "value"));
				Thread.sleep(1000);
				break;
			} catch (Exception e) {
				System.out.println(i + "fetching element again" + e);
				i++;
				if (i == 20)
					break;
			}
		}
		Thread.sleep(1000);
	}

	public void SearchAndSelectPerNo(String xpath, String data) throws InterruptedException
	{

		System.out.println("Search and Select Input "+data);
		clickElement(xpath);

		//clickElement(".//a[text()='Search...'  and contains(@id,'dropdownPopup::popupsearch')]");
		try {
			inputElement(".//input[contains(@id,'it5::content')]",data);
			clickElement(".//button[contains(@id,'cb3')]");
			clickElement(".//*[contains(@id,'t1::db')]/table/tbody/tr[1]/td[2]/div/table/tbody/tr[1]/td[1]/span/a[4]");
			//clickElement(".//button[contains(@id,'lovDialogId::ok')]");
			//clickElement(".//*[contains(@id,'t1::db')]/table/tbody/tr[1]/td[2]/div/table/tbody/tr[1]/td[1]");
			//waitUntilXpathInvisible(".//*[contains(@id,'t1::db')]/table/tbody/tr[1]/td[2]/div/table/tbody/tr[1]/td[1]");

			waitUntilXpathInvisible(".//*[contains(@id,'t1::db')]/table/tbody/tr[1]/td[2]/div/table/tbody/tr[1]/td[1]/span/a[4]");
		}catch(Exception e)
		{
			clickElement(".//button[contains(@id,'AP3:d1::cancel')]");
		}
	}

	public void SearchAndSelectFind(String xpath, String data) throws InterruptedException
	{

		System.out.println("Search and Select Input "+data);
		//clickElement(xpath);
		System.out.println("Adding Input:"+data+"In "+xpath);
		By xPath = By.xpath(xpath);		
		wait.until(ExpectedConditions.visibilityOfElementLocated(xPath));
		driver.findElement(xPath).clear();
		driver.findElement(xPath).sendKeys(data,Keys.TAB);	
		if(!whetherXpathExist(".//*[contains(@id,'t1::db')]/table/tbody/tr[1]/td[2]/div/table/tbody/tr[1]/td[1]/span/a", 10))
		{
			return;
		}
		try {

			clickElement(".//*[contains(@id,'t1::db')]/table/tbody/tr[1]/td[2]/div/table/tbody/tr[1]/td[1]/span/a");

			waitUntilXpathInvisible(".//*[contains(@id,'t1::db')]/table/tbody/tr[1]/td[2]/div/table/tbody/tr[1]/td[1]/span/a");

		}catch(Exception e)
		{
			clickElement(".//button[contains(@id,':cancelBtn1')]");

		}


	}

	public void selectRadioButton(String xpath,String data)
	{
		// Find the checkbox or radio button element by Name
		System.out.println(" before selectRadioButton"+xpath+" data: "+data);

		List<WebElement> oCheckBox = driver.findElements(By.xpath(xpath));

		// This will tell you the number of checkboxes are present

		int iSize = oCheckBox.size();


		for(int i=0; i < iSize ; i++ ){

			// Store the checkbox name to the string variable, using 'Value' attribute

			String sValue = oCheckBox.get(i).getAttribute("value");
			System.out.println("Value:"+sValue+"Data "+data);
			// Select the checkbox it the value of the checkbox is same what you are looking for

			if (sValue.equalsIgnoreCase(data)){
				System.out.println("before selecting RadioButton"+xpath+" data: "+data);
				oCheckBox.get(i).click();
				System.out.println("After selectRadioButton"+xpath+" data: "+data);
				break;
			}

		}

	}

	public void switchWindow(String title) throws InterruptedException {
		String currentWindow = driver.getWindowHandle();
		int tries = 0;
		while(!driver.getTitle().equalsIgnoreCase(title) && tries<4) {
			for(String winHandle : driver.getWindowHandles()){
				if (driver.switchTo().window(winHandle).getTitle().equals(title)) {
					logger.info("Switched window");
					return;
				} 
				driver.switchTo().window(currentWindow);

			}
			Thread.sleep(1000);
			tries++;
		}
	}

	public WebElement firstVisibleAndClickableElement(String xpath) {
		Wait<WebDriver> shortWait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(1))
				.ignoring(NoSuchElementException.class);
		By byElement = By.xpath(xpath);
		List<WebElement> elements = driver.findElements(byElement);
		for (WebElement webElement : elements) {
			try {
				shortWait.until(ExpectedConditions.visibilityOf(webElement));
				shortWait.until(ExpectedConditions.elementToBeClickable(webElement));
				return webElement;
			}catch(Exception e) {
			}
		}
		return null;
	}

}