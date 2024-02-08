package com.kdd.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.kdd.actions.ActionsClass;
import com.kdd.exceptions.InvalidKeywordException;
import com.kdd.reports.ReportManager;
import com.kdd.utility.DateUtility;
import com.kdd.utility.ExcelReader;
import com.kdd.utility.ScreenshotUtility;

/**
 * @author Bheemarao.M
 *
 */
public class Executor implements GlobalVariables {

	private static final Logger Log = Logger.getLogger(Executor.class.getName());

	public Map<Integer, String> getListOfTestCasesToExecute() {
		Log.info("Getting List of Test Cases to execute.");
		return new ExcelReader().getTestCasesToRun(testDataSheet, runModeColumn, testCaseColumn);
	}

	public void executeTestCase(String testCaseID,ITestContext context) throws Exception {
		String keyword;
		String locType;
		String locValue;
		String value;
		String runtimeValue;
		String stepDesc;
		WebDriver driver;
		try {
			int numberOfRows = ExcelReader.getNumberOfRows(testCaseID);
			for (int iRow = 1; iRow < numberOfRows; iRow++) {
				driver = DriverManager.getInstance().getDriver();
				keyword = ExcelManager.getInstance().getExcelReader().getCellData(iRow, keywordColumn, testCaseID);
				locType = ExcelManager.getInstance().getExcelReader().getCellData(iRow, locatorType, testCaseID);
				locValue = ExcelManager.getInstance().getExcelReader().getCellData(iRow, locatorValue, testCaseID);
				value = ExcelManager.getInstance().getExcelReader().getCellData(iRow, dataColumn, testCaseID);
				runtimeValue = ExcelManager.getInstance().getExcelReader().getCellData(iRow, runTimePropColumn,
						testCaseID);
				if (value.contains("##")) {
					String regEx = "\\d+";
					String sub = value.substring(0, 3);
					if (sub.matches(regEx)) {
						long number = DateUtility.generateRandomNumber();
						value = sub.concat(Long.toString((number)));
					} else {
						int len = value.substring(2, value.length()).length();
						String alpha = DateUtility.generateRandomString(len);
						value = value.substring(0, 2).concat(alpha);
					}
				}
				switch (value) {
				case "CURRENT_SYSDATE":
					value = DateUtility.getCurrentSystemDate(0);
					break;
				case "CURRENT_SYSDATE+1":
					value = DateUtility.getCurrentSystemDate(1);
					break;
				case "CURRENT_SYS_STARTTIME":
					value = DateUtility.addMinutesToCurrentTime(5);
					break;
				case "CURRENT_SYS_ENDTIME":
					value = DateUtility.addMinutesToCurrentTime(15);
					break;
				case "RANDOM_EMAIL":
					value = DateUtility.generateRandomEmail(10);
					break;
				}

				if (!runtimeValue.equals("NA")) {
					runtimeValue = runtimeValue.concat(testCaseID);
					PropertiesConfig.setRunTimeProperty(runtimeValue, value);
				}

				if (value.startsWith("RUNTIME_")) {
					String updatedKey = value.concat(testCaseID);
					value = PropertiesConfig.getRunTimeProperty(updatedKey);
				}

				stepDesc = ExcelManager.getInstance().getExcelReader().getCellData(iRow, testStepDescriptionColumn,
						testCaseID);
				Log.info("Keyword: " + keyword + " Value: " + value);
				try {
					executeAction(keyword, driver, locType, locValue, value, runtimeValue);
				} catch (Exception e) {
					Log.error("Exception Occurred while executing the step:\n", e);
					String imageFilePath = ScreenshotUtility.takeScreenShot(DriverManager.getInstance().getDriver(),
							testCaseID);
					context.setAttribute("IMAGEFILEPATH"+testCaseID, imageFilePath);
					ReportManager.getTest().fail(stepDesc + "<br/>Keyword: " + keyword + " | Value: " + value,
							MediaEntityBuilder.createScreenCaptureFromPath(imageFilePath).build());
					context.setAttribute("FAILEDSTEPID"+testCaseID, Integer.toString(iRow+1));
					ReportManager.getTest().getModel();
					ReportManager.getTest().info(e);
					throw e;
				}
				if (stepDesc != null && !stepDesc.isEmpty()) {
					ReportManager.getTest().pass(stepDesc + "<br/>Keyword: " + keyword + " | Value: " + value);
				}

				if (iRow == numberOfRows - 1) {
					String imageFilePath = ScreenshotUtility.takeScreenShot(DriverManager.getInstance().getDriver(),
							testCaseID);
					context.setAttribute("IMAGEFILEPATH"+testCaseID, imageFilePath);
					ReportManager.getTest().pass(stepDesc + "<br/>Keyword: " + keyword + " | Value: " + value,
							MediaEntityBuilder.createScreenCaptureFromPath(imageFilePath).build());
					context.setAttribute("ALLSTEPSPASSID"+testCaseID, Integer.toString(iRow+1));
				}
				
			}
		} catch (Exception e) {
			int testCaseRow = (Integer) SessionDataManager.getInstance().getSessionData("testCaseRow");
			ExcelManager.getInstance().getExcelReader().setCellData(FAIL, testCaseRow, resultColumn, testDataPath,
					testDataSheet);
			throw e;
		}
	}

	private void executeAction(String keyword, WebDriver driver, String locType, String locValue, String value,
			String runtimeValue) throws Exception {
		ActionsClass keywordActions = new ActionsClass();
		Method method[] = keywordActions.getClass().getMethods();
		boolean keywordFound = false;
		for (int i = 0; i < method.length; i++) {
			if (method[i].getName().equals(keyword)) {
				method[i].invoke(keywordActions, driver, locType, locValue, value, runtimeValue);
				keywordFound = true;
				break;
			}
		}
		if (!keywordFound) {
			throw new InvalidKeywordException("Invalid Keyword " + keyword);
		}
	}
	

}
