package com.kdd.runner;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.internal.BaseTestMethod;

import com.kdd.config.PropertiesConfig;
import com.kdd.config.ReportDirectory;

import com.kdd.config.DriverManager;
import com.kdd.config.GlobalVariables;
import com.kdd.config.SessionDataManager;
import com.kdd.reports.ReportManager;
import com.kdd.utility.DateUtility;
import com.kdd.utility.ExcelReader;
import com.kdd.utility.Log;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * @author Bheemarao.M
 *
 */
public class TestBase implements GlobalVariables {

	private static final Logger Logs = Logger.getLogger(TestBase.class.getName());

	@BeforeSuite
	public void configurations(ITestContext context) {
		DOMConfigurator.configure("log4j.xml");
		Logs.info("Setting up Test data Excel sheet");
		ReportDirectory directory = new ReportDirectory();
		directory.clearFolder(screenshotFolder);
		directory.clearFolder(htmlReportPath);
		ExcelReader.setExcelFile(testDataPath);
		ExcelReader.clearColumnData(testDataSheet, resultColumn, testDataPath);
		if (PropertiesConfig.getProperty("UpdateResultsInAzure").equalsIgnoreCase("true")) {
			String testRunID = createTestRunID(PropertiesConfig.getProperty("AzureTestPlanID"),
					PropertiesConfig.getProperty("AzureTestSuiteID"));
			context.setAttribute("TESTRUNID", testRunID);
		}

	}

	@BeforeMethod
	public void initialization(Method method, Object[] testData, ITestContext ctx) {
		WebDriver driver = launchBrowser();
		DriverManager.getInstance().setDriver(driver);
	}

	@AfterMethod
	public void updateStatus(ITestResult result, ITestContext context) throws Exception {
		BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod().clone();
		String testID = (String) result.getParameters()[1];
		Integer row = (Integer) result.getParameters()[0];
		String testTitle = ExcelReader.getTestTitle(testDataSheet, row.intValue());
		Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
		f.setAccessible(true);
		f.set(baseTestMethod, testTitle);
		f = result.getClass().getDeclaredField("m_method");
		f.setAccessible(true);
		f.set(result, baseTestMethod);
		String status = SKIP;

		if (result.getStatus() == ITestResult.FAILURE) {
			status = FAIL;
		}
		if (result.getStatus() == ITestResult.SUCCESS) {
			status = PASS;
		}

		String failedStep = (String) context.getAttribute("FAILEDSTEPID" + testID);
		String allStepsPass = (String) context.getAttribute("ALLSTEPSPASSID" + testID);

		if (PropertiesConfig.getProperty("UpdateResultsInAzure").equalsIgnoreCase("true")) {
			String testPlanID = PropertiesConfig.getProperty("AzureTestPlanID");
			String testSuiteID = PropertiesConfig.getProperty("AzureTestSuiteID");
			String testRunID = (String) context.getAttribute("TESTRUNID");
			String testPointID = getTestPoint(testPlanID, testSuiteID, testID);
			String testResultsID = createResultID(testRunID, testID, testPointID, testTitle, status);
			if (status.equalsIgnoreCase("Passed")) {
				updateAzureTestRunResults(testPlanID, testRunID, testSuiteID, testID, testPointID, testResultsID,
						status, Integer.parseInt(allStepsPass));
			} else {
				updateAzureTestRunResults(testPlanID, testRunID, testSuiteID, testID, testPointID, testResultsID,
						status, Integer.parseInt(failedStep));
			}
			createTestResultAttachment(testRunID, testResultsID, testID,
					(String) context.getAttribute("IMAGEFILEPATH" + testID));
			updateTestResultID(testRunID, testResultsID);
		}
		Logs.info("Closing all the browser.");
		String testCaseName = (String) SessionDataManager.getInstance().getSessionData("testCaseName");
		ReportManager.endTest();
		DriverManager.getInstance().getDriver().quit();
		Log.endTestCase(testCaseName + " " + status);
	}

	private static WebDriver launchBrowser() {
		String browser = PropertiesConfig.getProperty("browser");
		WebDriver driver = null;
		if (browser.equalsIgnoreCase("CHROME")) {
			WebDriverManager.chromedriver().setup();
			ChromeOptions opt = new ChromeOptions();
			opt.addArguments("--window-size=1920,1080");
			opt.addArguments("--start-maximized");
			opt.addArguments("--remote-allow-origins=*");
			driver = new ChromeDriver(opt);
		}
		driver.get(PropertiesConfig.getProperty("baseURL"));
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitTime));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(implicitWaitTime));
		return driver;
	}

	public void updateAzureTestRunResults(String testPlanID, String testRunID, String testSuiteID, String testID,
			String testPointID, String testResultsID, String status, int step) {
		RestAssured.baseURI = PropertiesConfig.getProperty("AzureBaseURI");
		String path = "/_apis/test/Runs/" + testRunID + "/results?api-version=7.0";
		String body = "";
		if (status.equalsIgnoreCase("Passed")) {
			String actionList = generatePASSJsonBody(step, status);
			body = "[{\"id\":TESTRESULTSID,\"outcome\": \"Passed\",\"testCase\":{\"id\":\"TESTID\"},\"testPoint\":{\"id\": \"TESTPOINTID\"},\"testPlan\":{\"id\":\"TESTPLANID\"},\"testSuite\":{\"id\": \"TESTSUITEID\"},\"iterationDetails\":[{\"id\": 1,\"outcome\": \"Passed\",\"actionResults\":[ACTIONBODY]}]}]"
					.replace("TESTRESULTSID", testResultsID).replace("TESTID", testID)
					.replace("TESTPOINTID", testPointID).replace("TESTSUITEID", testSuiteID)
					.replace("TESTPLANID", testPlanID).replace("ACTIONBODY", actionList).replace("STATUS", status);
		} else {
			String actionList = generateFailJsonBody(step, status);
			body = "[{\"id\":TESTRESULTSID,\"outcome\": \"Failed\",\"testCase\":{\"id\":\"TESTID\"},\"testPoint\":{\"id\": \"TESTPOINTID\"},\"testPlan\":{\"id\":\"TESTPLANID\"},\"testSuite\":{\"id\": \"TESTSUITEID\"},\"iterationDetails\":[{\"id\": 1,\"outcome\": \"Failed\",\"actionResults\":[ACTIONBODY]}]}]"
					.replace("TESTRESULTSID", testResultsID).replace("TESTID", testID)
					.replace("TESTPOINTID", testPointID).replace("TESTSUITEID", testSuiteID)
					.replace("TESTPLANID", testPlanID).replace("ACTIONBODY", actionList).replace("STATUS", status);
		}
		Response response = given().auth().preemptive()
				.basic("", PropertiesConfig.getProperty("AzurePersonalAccessToken"))
				.header("Accept", ContentType.JSON.getAcceptHeader()).contentType(ContentType.JSON).body(body)
				.patch(path).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	public String generatePASSJsonBody(int row, String status) {
		String body = "{\"actionPath\":\"STEPHEXAVALUE\",\"iterationId\": 1,\"stepIdentifier\": \"STEP\",\"outcome\": \"STATUS\"}";
		String updatedBody = body;
		for (int i = 2; i <= row; i++) {
			updatedBody = updatedBody.replace("STEPHEXAVALUE", StringUtils.leftPad(Integer.toString(i, 16), 8, '0'))
					.replace("STEP", Integer.toString(i));
			if (i < row) {
				updatedBody = updatedBody + ",";
				updatedBody = updatedBody + body;
			}
			updatedBody = updatedBody.replace("STATUS", status);
		}
		return updatedBody;
	}

	public String generateFailJsonBody(int row, String status) {
		String body = "{\"actionPath\":\"STEPHEXAVALUE\",\"iterationId\": 1,\"stepIdentifier\": \"STEP\",\"outcome\": \"STATUS\"}";
		String updatedBody = body;
		for (int i = 2; i < row; i++) {
			updatedBody = updatedBody.replace("STEPHEXAVALUE", StringUtils.leftPad(Integer.toString(i, 16), 8, '0'))
					.replace("STEP", Integer.toString(i));
			if (i < row - 1) {
				updatedBody = updatedBody + ",";
				updatedBody = updatedBody + body;
			}
			updatedBody = updatedBody.replace("STATUS", "Passed");
		}
		updatedBody = updatedBody + "," + body;
		updatedBody = updatedBody.replace("STEPHEXAVALUE", StringUtils.leftPad(Integer.toString(row, 16), 8, '0'))
				.replace("STEP", Integer.toString(row)).replace("STATUS", "Failed");
		return updatedBody;
	}

	public String getTestPoint(String testPlanID, String testSuiteID, String testID) {
		String testPoint = null;
		RestAssured.baseURI = PropertiesConfig.getProperty("AzureBaseURI");
		String path = "/_apis/test/points?api-version=7.0";
		String body = "{\"PointsFilter\":{\"TestcaseIds\":[TESTID],\"TestcaseIds\":[TESTSUITEID],\"TestcaseIds\":[TESTPLANID]}}"
				.replace("TESTID", testID).replace("TESTSUITEID", testSuiteID).replace("TESTPLANID", testPlanID);
		Response response = given().auth().preemptive()
				.basic("", PropertiesConfig.getProperty("AzurePersonalAccessToken"))
				.header("Accept", ContentType.JSON.getAcceptHeader()).contentType(ContentType.JSON).body(body)
				.post(path).then().extract().response();
		if (response.getStatusCode() == 200) {
			JsonPath jsonPathEvaluator = response.jsonPath();
			testPoint = jsonPathEvaluator.get("points[0].id").toString();
		}
		return testPoint;
	}

	public String createTestRunID(String testPlanID, String testSuiteID) {
		String testRun = null;
		RestAssured.baseURI = PropertiesConfig.getProperty("AzureBaseURI");
		String path = "/_apis/test/runs?api-version=7.0";
		String body = "{\"name\": \"AutomationTestRun\",\"comment\":\"Test Automation Run\",\"automated\":\"true\",\"state\":\"InProgress\",\"plan\": {\"id\": \"TESTPLANID\"},\"testSuite\": {\"id\": \"TESTSUITEID\"}}"
				.replace("TESTSUITEID", testSuiteID).replace("TESTPLANID", testPlanID);
		Response response = given().auth().preemptive()
				.basic("", PropertiesConfig.getProperty("AzurePersonalAccessToken"))
				.header("Accept", ContentType.JSON.getAcceptHeader()).contentType(ContentType.JSON).body(body)
				.post(path).then().extract().response();
		if (response.getStatusCode() == 200) {
			JsonPath jsonPathEvaluator = response.jsonPath();
			testRun = jsonPathEvaluator.get("id").toString();
		}
		return testRun;
	}

	public String createResultID(String testRunID, String testID, String testPointID, String testTitle, String status) {
		String resultID = null;
		RestAssured.baseURI = PropertiesConfig.getProperty("AzureBaseURI");
		String path = "/_apis/test/Runs/" + testRunID + "/results?api-version=7.0";
		String body = "[{\"outcome\":\"STATUS\",\"comment\":\"Testcase ran from Azure Pipeline\",\"state\":\"InProgress\",\"testPoint\":{\"id\":\"TESTPOINTSID\"},\"testCase\":{\"id\":\"TESTID\"},\"testCaseTitle\": \"TESTTITLE\",\"testCaseRevision\":1}]"
				.replace("TESTPOINTSID", testPointID).replace("TESTID", testID).replace("TESTTITLE", testTitle)
				.replace("STATUS", status);
		Response response = given().auth().preemptive()
				.basic("", PropertiesConfig.getProperty("AzurePersonalAccessToken"))
				.header("Accept", ContentType.JSON.getAcceptHeader()).contentType(ContentType.JSON).body(body)
				.post(path).then().extract().response();
		if (response.getStatusCode() == 200) {
			JsonPath jsonPathEvaluator = response.jsonPath();
			resultID = jsonPathEvaluator.get("value[0].id").toString();
		}
		return resultID;
	}

	public String getBase64Stream(String imgPath) throws FileNotFoundException, IOException {
		File file = new File(imgPath);
		byte[] data = new byte[(int) file.length()];
		try (FileInputStream fis = new FileInputStream(file)) {
			fis.read(data);
		}
		return Base64.getEncoder().encodeToString(data);
	}

	public void createTestResultAttachment(String testRunID, String testResultsID, String testID, String imgPath)
			throws FileNotFoundException, IOException {
		RestAssured.baseURI = PropertiesConfig.getProperty("AzureBaseURI");
		String path = "/_apis/test/Runs/" + testRunID + "/Results/" + testResultsID + "/attachments?api-version=7.0";
		String stream = getBase64Stream(htmlReportPath + imgPath);
		String body = "{\"stream\": \"BASE64STREAM\",\"fileName\": \"PATH\",\"comment\": \"Test attachment upload\",\"attachmentType\": \"GeneralAttachment\"}"
				.replace("BASE64STREAM", stream).replace("PATH", imgPath);
		Response response = given().auth().preemptive()
				.basic("", PropertiesConfig.getProperty("AzurePersonalAccessToken"))
				.header("Accept", ContentType.JSON.getAcceptHeader()).contentType(ContentType.JSON).body(body)
				.post(path).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	public void updateTestResultID(String testRunID, String testResultsID) {
		RestAssured.baseURI = PropertiesConfig.getProperty("AzureBaseURI");
		String path = "/_apis/test/Runs/" + testRunID + "/results?api-version=7.0";
		String body = "[{\"id\": TESTRESULTID,\"state\": \"Completed\"}]".replace("TESTRESULTID", testResultsID);
		Response response = given().auth().preemptive()
				.basic("", PropertiesConfig.getProperty("AzurePersonalAccessToken"))
				.header("Accept", ContentType.JSON.getAcceptHeader()).contentType(ContentType.JSON).body(body)
				.patch(path).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 200);
	}

	@AfterSuite
	public void tearDown(ITestContext context) {
		RestAssured.baseURI = PropertiesConfig.getProperty("AzureBaseURI");
		String testRun = (String) context.getAttribute("TESTRUNID");
		String path = "/_apis/test/runs/" + testRun + "?api-version=7.0";
		String body = "{\"startedDate\":\"TODAY\",\"completedDate\":\"TODAY\",\"state\":\"Completed\",\"postProcessState\":\"Complete\"}"
				.replace("TODAY", DateUtility.getStringDate("yyyy-MM-dd"));
		Response response = given().auth().preemptive()
				.basic("", PropertiesConfig.getProperty("AzurePersonalAccessToken"))
				.header("Accept", ContentType.JSON.getAcceptHeader()).contentType(ContentType.JSON).body(body)
				.patch(path).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 200);
	}
}
