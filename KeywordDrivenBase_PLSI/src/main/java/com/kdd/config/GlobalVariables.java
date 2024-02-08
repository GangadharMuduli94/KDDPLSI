package com.kdd.config;

/**
 * @author Bheemarao.M
 *
 */
public interface GlobalVariables {

	String baseDirectory = System.getProperty("user.dir");
	String configPath = baseDirectory + "/config.properties";
	String configPathRuntime = baseDirectory + "/configruntime.properties";

	// Path to test data sheet with test cases to run and test case details
	String testDataPath = baseDirectory + "/src/test/resources/data/TestDataSheet.xlsx";
	String testDataSheet = "TestCases";
	String testPlanSheet="TestPlanDetails";
	int testCaseColumn = 0;
	int testCaseDescriptionColumn = 1;
	int runModeColumn = 2;
	int resultColumn = 3;

	// Columns in Test Case sheet
	int testStepsColumn = 0;
	int testStepDescriptionColumn = 1;
	int keywordColumn = 2;
	int locatorType = 3;
	int locatorValue = 4;
	int dataColumn = 5;
	int runTimePropColumn = 6;

	String PASS = "Passed";
	String FAIL = "Failed";
	String SKIP = "Skipped";

	// Wait times
	int implicitWaitTime = 40;
	long explictWaitTime = 50;
	int fluentWaitTime = 20;
	int poolTime = 5;

	// Screenshot folder and file details
	String screenshotFolder = "target/html-report/";
	String fileFormat = ".png";

	// Report related details
	String extentConfigFilePath = baseDirectory + "/extent-config.xml";
	String htmlReportPath = "target/html-report/";
	String htmlFileName = "index.html";
}
