package com.kdd.utility;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.kdd.config.GlobalVariables;
import com.kdd.exceptions.InvalidLocatorException;

/**
 * @author Bheemarao.M
 *
 */
public class ElementOperations implements GlobalVariables {

	public WebElement getElement(WebDriver driver, String locType, String locValue) throws InvalidLocatorException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(explictWaitTime));
		By element = getElementBy(locType.trim(), locValue.trim());
		WebElement webElement=wait.until(ExpectedConditions.presenceOfElementLocated(element));
		return webElement;
	}
	
	public List<WebElement> getElements(WebDriver driver, String locType, String locValue) throws InvalidLocatorException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(explictWaitTime));
		By element = getElementBy(locType.trim(), locValue.trim());
		List<WebElement>  webElements=wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(element));
		return webElements;
	}

	public By getElementBy(String locator, String selector) throws InvalidLocatorException {
		By byLocator = null;
		switch (locator) {
		case "id": {
			byLocator = By.id(selector);
			break;
		}
		case "name": {
			byLocator = By.name(selector);
			break;
		}
		case "partialLinkText": {
			byLocator = By.partialLinkText(selector);
			break;
		}
		case "linkText": {
			byLocator = By.linkText(selector);
			break;
		}
		case "className": {
			byLocator = By.className(selector);
			break;
		}
		case "tagName": {
			byLocator = By.tagName(selector);
			break;
		}
		case "cssSelector": {
			byLocator = By.cssSelector(selector);
			break;
		}
		case "xpath": {
			byLocator = By.xpath(selector);
			break;
		}
		default: {
			throw new InvalidLocatorException("Invalid Locator type " + locator);
		}
		}
		return byLocator;
	}

}
