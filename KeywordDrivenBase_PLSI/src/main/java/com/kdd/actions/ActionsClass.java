package com.kdd.actions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.kdd.config.GlobalVariables;
import com.kdd.config.PropertiesConfig;
import com.kdd.exceptions.InvalidLocatorException;
import com.kdd.utility.ElementOperations;

/**
 * @author Bheemarao.M
 *
 */
public class ActionsClass extends ElementOperations implements GlobalVariables {
	private static final Logger Logs = Logger.getLogger(ActionsClass.class.getName());

	/**
	 * Keyword: enterText()
	 * 
	 * This keyword is used to Enters Data in to the Text Box
	 *
	 */
	public void enterText(WebDriver driver, String locatorType, String locatorValue, String data, String runTimeValue)
			throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			element.clear();
			element.sendKeys(data);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'enterText' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: selectValueFromDropdownUsingSendKeys()
	 * 
	 * This keyword is used to Select Value from the DropDown
	 *
	 */

	public void selectValueFromDropdownUsingSendKeys(WebDriver driver, String locatorType, String locatorValue,
			String value, String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Thread.sleep(2000);
			element.click();
			element.sendKeys(value);
			element.sendKeys(Keys.TAB);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'selectValueFromDropdownUsingSendKeys' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: verifyCreatedRecordInTheTable()
	 * 
	 * This keyword is used to Verify Created Record In the Table
	 *
	 */

	public void verifyCreatedRecordInTheTable(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Thread.sleep(10000);
			element = getElement(driver, locatorType, locatorValue);
			Assert.assertTrue(element.getText().toUpperCase().contains(data.toUpperCase()));
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'verifyCreatedRecordInTheTable' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: verifyNoRecordsInTheTable()
	 * 
	 * This keyword is used to Verify No records Created Record In the Table
	 *
	 */

	public void verifyNoRecordsInTheTable(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			List<WebElement> elements = driver.findElements(getElementBy(locatorType, locatorValue));
			Assert.assertTrue(elements.size() < 1);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'verifyNoRecordsInTheTable' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: clickElement()
	 * 
	 * This keyword is used to Click any web element
	 *
	 */
	public void clickElement(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			if (element.isDisplayed()) {
				element.click();
			}
		} catch (ElementClickInterceptedException e) {
			Thread.sleep(10000);
			WebElement element = getElement(driver, locatorType, locatorValue);
			if (element.isDisplayed()) {
				element.click();
			}
		} catch (StaleElementReferenceException e) {
			Thread.sleep(10000);
			WebElement element = getElement(driver, locatorType, locatorValue);
			if (element.isDisplayed()) {
				element.click();
			}
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'clickElement' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: verifyElementDisplayed()
	 * 
	 * This keyword is used to Verify Element is Displayed
	 *
	 */

	public void verifyElementDisplayed(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Thread.sleep(10000);
			Assert.assertTrue(element.isDisplayed());
		} catch (StaleElementReferenceException e) {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Thread.sleep(10000);
			Assert.assertTrue(element.isDisplayed());
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'verifyElementDisplayed' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Keyword: verifyElementsDisplayed()
	 * 
	 * This keyword is used to Verify List of Elements is Displayed
	 *
	 */

	public void verifyElementsDisplayed(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			List<WebElement> elements = getElements(driver, locatorType, locatorValue);
			Thread.sleep(2000);
			Assert.assertTrue(!elements.isEmpty());
		} catch (StaleElementReferenceException e) {
			List<WebElement> elements = getElements(driver, locatorType, locatorValue);
			Thread.sleep(2000);
			Assert.assertTrue(!elements.isEmpty());
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'verifyElementsDisplayed' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: clickElementJavaScript()
	 * 
	 * This keyword is used to Click web element using Java Script
	 *
	 */

	public void clickElementJavaScript(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			WebElement element = getElement(driver, locatorType, locatorValue);
			executor.executeScript("arguments[0].click();", element);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'clickElementJavaScript' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: scrollToElementJavaScript()
	 * 
	 * This keyword is used to Scroll to the Web Element using Java Script
	 *
	 */
	public void scrollToElementJavaScript(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView();", element);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'scrollToElementJavaScript' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: getValueFromTheTable()
	 * 
	 * This keyword is used to Get the Value from the table and Store into Runtime
	 * Config Properties
	 *
	 */

	public void getValueFromTheTable(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Thread.sleep(10000);
			element = getElement(driver, locatorType, locatorValue);
			PropertiesConfig.setRunTimeProperty(runTimeValue, element.getText());
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'getValueFromTheTable' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: verifyElementNotDisplayed()
	 * 
	 * This keyword is used to Verify Element is Not Displayed
	 *
	 */
	public void verifyElementNotDisplayed(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			Thread.sleep(10000);
			List<WebElement> elements = driver.findElements(getElementBy(locatorType, locatorValue));
			Assert.assertTrue(elements.size() < 1);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'verifyElementNotDisplayed' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: navigateToURL()
	 * 
	 * This keyword is used to Navigate to Specific URL
	 *
	 */
	public void navigateToURL(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			driver.get(data);
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitTime));
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(implicitWaitTime));
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'navigateToURL' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: clearTextJavaScript()
	 * 
	 * This keyword is used to Clear text in Text Box using java Script
	 *
	 */

	public void clearTextJavaScript(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].value='';", element);
			Thread.sleep(3000);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'clearTextJavaScript' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: clearText()
	 * 
	 * This keyword is used to Clear text in Text Box
	 *
	 */
	public void clearText(WebDriver driver, String locatorType, String locatorValue, String data, String runTimeValue)
			throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Actions actions = new Actions(driver);
			actions.click(element).keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE)
					.build().perform();
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'clearText' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: verifyElementSelected()
	 * 
	 * This keyword is used to Verify Check box/Radio button is Selected
	 *
	 */

	public void verifyElementSelected(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Assert.assertEquals(true, element.isSelected());
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'verifyElementSelected' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: clickElementAndHold()
	 * 
	 * This keyword is used to Click and Hold the web element-Mouse Click
	 *
	 */

	public void clickElementAndHold(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			new Actions(driver).clickAndHold(element).perform();
			Assert.assertTrue(true);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'clickElementAndHold' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: clickElementAndRelease()
	 * 
	 * This keyword is used to Click and Release the web element- Mouse Click
	 *
	 */

	public void clickElementAndRelease(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			new Actions(driver).click(element).perform();
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'clickElementAndRelease' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: rightClickElement()
	 * 
	 * This keyword is used to Right Click the web element- Mouse Click
	 *
	 */

	public void rightClickElement(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			new Actions(driver).contextClick(element).perform();
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'rightClickElement' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: doubleClickElement()
	 * 
	 * This keyword is used to Double Click the web element- Mouse Click
	 *
	 */

	public void doubleClickElement(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			new Actions(driver).doubleClick(element).perform();
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'doubleClickElement' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: mouseHoverElement()
	 * 
	 * This keyword is used to Mouse hover to the web element
	 *
	 */

	public void mouseHoverElement(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			new Actions(driver).moveToElement(element).perform();
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'mouseHoverElement' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: scrollToElement()
	 * 
	 * This keyword is used to Scroll to the web element
	 *
	 */

	public void scrollToElement(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			new Actions(driver).scrollToElement(element).perform();
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'scrollToElement' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: verifyTextDisplayedWithGivenValue()
	 * 
	 * This keyword is used to Verify text Displayed with given Value
	 *
	 */

	public void verifyTextDisplayedWithGivenValue(WebDriver driver, String locatorType, String locatorValue,
			String data, String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Assert.assertEquals(element.getText(), data);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'verifyTextDisplayedWithGivenValue' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: refreshPage()
	 * 
	 * This keyword is used to Refresh Page
	 *
	 */
	public void refreshPage(WebDriver driver, String locatorType, String locatorValue, String data, String runTimeValue)
			throws Exception {
		try {
			driver.navigate().refresh();
			Thread.sleep(10000);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'refreshPage' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}

	/**
	 * Keyword: verifyClassAttributeOfTheElement()
	 * 
	 * This keyword is used to verify any attribute of the element (to check
	 * background and text colors)
	 *
	 */
	public void verifyClassAttributeOfTheElement(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Assert.assertTrue(element.getAttribute("class").equalsIgnoreCase(data));
		} catch (StaleElementReferenceException e) {
			Thread.sleep(5000);
			WebElement element = getElement(driver, locatorType, locatorValue);
			Assert.assertTrue(element.getAttribute("class").equalsIgnoreCase(data));
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'verifyClassAttributeOfTheElement' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Keyword: VerifyTableSortedByGivenColumn()
	 * 
	 * This keyword is used to verify table is sorted
	 *
	 */
	public void verifyTableSortedByGivenColumn(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			List<WebElement> elements = getElements(driver, locatorType, locatorValue);
			List<String> valCol=new ArrayList<String>();
			for(WebElement ele:elements) {
				valCol.add(ele.getText().toUpperCase());
			}
		    Assert.assertTrue(valCol.stream().sorted().collect(Collectors.toList()).equals(valCol));
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'VerifyTableSortedByGivenColumn' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}
	
	
	/**
	 * Keyword: VerifySearchWithResultsInTable()
	 * 
	 * This keyword is used to verify search Functionality with Results
	 *
	 */
	public void verifySearchWithResultsInTable(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			int rowCount=getElements(driver, locatorType, locatorValue+"//tr").size();
			int colCount=getElements(driver, locatorType, locatorValue+"//tr[1]//td").size();
			boolean found=false;
			for(int i=1;i<rowCount;i++) {
				found=false;
				for(int j=1;j<colCount;j++) {
					String text=getElement(driver, locatorType, locatorValue+"//tr["+i+"]//td["+j+"]").getText();
					if(text.contains(data)) {
						found=true;
						break;
					}
				}	
			}
			Assert.assertTrue(found);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'VerifySearchWithResultsInTable' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Keyword: VerifySearchWithNoResultsInTable()
	 * 
	 * This keyword is used to verify search Functionality with No Results
	 *
	 */
	public void VerifySearchWithNoResultsInTable(WebDriver driver, String locatorType, String locatorValue, String data,
			String runTimeValue) throws Exception {
		try {
			List<WebElement> elements = driver.findElements(getElementBy(locatorType, locatorValue));
			Assert.assertTrue(elements.size() < 1);
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'VerifySearchWithNoResultsInTable' action " + e.getMessage());
			Assert.assertFalse(false, e.getMessage());
			throw e;
		}
	}
	

	public void verifyAlertDisplayedAccept(WebDriver driver, String alertMessage) {
		String actualAlertMsg = driver.switchTo().alert().getText();
		driver.switchTo().alert().accept();
		Assert.assertEquals(actualAlertMsg, alertMessage);
	}

	public void verifyAlertDisplayedDismiss(WebDriver driver, String alertMessage) {
		String actualAlertMsg = driver.switchTo().alert().getText();
		driver.switchTo().alert().dismiss();
		Assert.assertEquals(actualAlertMsg, alertMessage);
	}

	public void verifyAlertDisplayedSendKeys(WebDriver driver, String alertMessage, String data) {
		String actualAlertMsg = driver.switchTo().alert().getText();
		driver.switchTo().alert().sendKeys(data);
		Assert.assertEquals(actualAlertMsg, alertMessage);
	}

	public void verifyCurrentURL(WebDriver driver, String URL) {
		Assert.assertEquals(driver.getCurrentUrl(), URL);
	}

	public void verifyTitle(WebDriver driver, String title) {
		Assert.assertEquals(driver.getTitle(), title);
	}

	public void navigateBack(WebDriver driver, String URL) {
		try {
			driver.navigate().back();
			Thread.sleep(2000);
			verifyCurrentURL(driver, URL);
		} catch (InterruptedException e) {
			Logs.error("Failing whie exeuting 'navigateBack' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void navigateForward(WebDriver driver, String URL) {
		try {
			driver.navigate().forward();
			Thread.sleep(2000);
			verifyCurrentURL(driver, URL);
		} catch (InterruptedException e) {
			Logs.error("Failing whie exeuting 'navigateForward' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void verifyDropdownLoaded(WebDriver driver, String locatorType, String locatorValue, String expectedValues) {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Select select = new Select(element);
			List<String> actual = new ArrayList<>();
			for (WebElement option : select.getOptions()) {
				actual.add(option.getText());
			}
			List<String> expected = Arrays.asList(expectedValues.split(";;"));
			Assert.assertTrue(actual.stream().allMatch(value -> expected.contains(value)));
		} catch (InvalidLocatorException e) {
			Logs.error("Failing whie exeuting 'verifyDropdownLoaded' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void selectValueFromDropdownByIndex(WebDriver driver, String locatorType, String locatorValue,
			String indexs) {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Select select = new Select(element);
			for (String index : indexs.split(";;")) {
				select.selectByIndex(Integer.valueOf(index));
				Thread.sleep(1000);
			}
			Assert.assertTrue(!select.getAllSelectedOptions().isEmpty());
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'selectValueFromDropdownByIndex' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void selectValueFromDropdownByValue(WebDriver driver, String locatorType, String locatorValue,
			String values) {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Select select = new Select(element);
			for (String value : values.split(";;")) {
				select.selectByValue(value);
				Thread.sleep(1000);
			}
			Assert.assertTrue(!select.getAllSelectedOptions().isEmpty());
		} catch (Exception e) {
			Logs.error("Failing whie exeuting 'selectValueFromDropdownByIndex' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void selectValueFromDropdownByVisibleText(WebDriver driver, String locatorType, String locatorValue,
			String texts) {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Select select = new Select(element);
			for (String text : texts.split(";;")) {
				select.selectByValue(text);
			}
			Assert.assertTrue(!select.getAllSelectedOptions().isEmpty());
		} catch (InvalidLocatorException e) {
			Logs.error("Failing whie exeuting 'selectValueFromDropdownByIndex' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void deselectValueFromDropdownByIndex(WebDriver driver, String locatorType, String locatorValue,
			String index) {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Select select = new Select(element);
			select.deselectByIndex(Integer.valueOf(index));
			Assert.assertTrue(true);
		} catch (InvalidLocatorException e) {
			Logs.error("Failing whie exeuting 'deselectValueFromDropdownByIndex' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void deselectValueFromDropdownByValue(WebDriver driver, String locatorType, String locatorValue,
			String value) {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Select select = new Select(element);
			select.deselectByValue(value);
			Assert.assertTrue(true);
		} catch (InvalidLocatorException e) {
			Logs.error("Failing whie exeuting 'deselectValueFromDropdownByValue' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void deselectValueFromDropdownByVisibleText(WebDriver driver, String locatorType, String locatorValue,
			String text) {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Select select = new Select(element);
			select.deselectByValue(text);
			Assert.assertTrue(true);
		} catch (InvalidLocatorException e) {
			Logs.error("Failing whie exeuting 'deselectValueFromDropdownByVisibleText' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void deselectAllValueFromDropdown(WebDriver driver, String locatorType, String locatorValue) {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			Select select = new Select(element);
			select.deselectAll();
			Assert.assertTrue(select.getAllSelectedOptions().isEmpty());
		} catch (InvalidLocatorException e) {
			Logs.error("Failing whie exeuting 'deselectAllValueFromDropdown' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

	public void switchToFrame(WebDriver driver, String locatorType, String locatorValue, String titleExpected) {
		try {
			WebElement frame = getElement(driver, locatorType, locatorValue);
			driver.switchTo().frame(frame);
			Assert.assertEquals(driver.getTitle(), titleExpected);
		} catch (InvalidLocatorException e) {
			Logs.error("Failing whie exeuting 'switchToFrame' action ");
			Assert.assertFalse(false, e.getMessage());
		}

	}

	public void switchToMainFrame(WebDriver driver, String titleExpected) {
		driver.switchTo().defaultContent();
		Assert.assertEquals(driver.getTitle(), titleExpected);
	}

	public void switchToParentFrame(WebDriver driver, String titleExpected) {
		driver.switchTo().parentFrame();
		Assert.assertEquals(driver.getTitle(), titleExpected);
	}

	public void switchToChildWindow(WebDriver driver, String locatorType, String locatorValue) {
		try {
			WebElement element = getElement(driver, locatorType, locatorValue);
			String parentWindow = driver.getWindowHandle();
			Set<String> allWindows = driver.getWindowHandles();
			for (String window : allWindows) {
				if (!window.equals(parentWindow)) {
					String childWindow = window;
					driver.switchTo().window(childWindow);
					Assert.assertTrue(element.isDisplayed());
					driver.close();
				}
			}
			driver.switchTo().window(parentWindow);
		} catch (InvalidLocatorException e) {
			Logs.error("Failing whie exeuting 'switchToChildWindow' action ");
			Assert.assertFalse(false, e.getMessage());
		}
	}

}
