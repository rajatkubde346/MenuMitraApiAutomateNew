package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.TableReservationRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.Listener;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class CheckTableIsReservedTestScript extends APIBase
{
    private TableReservationRequest checkTableIsReservedRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private JSONObject expectedJsonBody;
    Logger logger = LogUtils.getLogger(CheckTableIsReservedTestScript.class);
    
    @DataProvider(name = "getCheckTableIsReservedUrl")
    public Object[][] getCheckTableIsReservedUrl() throws customException {
        try {
            LogUtils.info("Reading Check Table Is Reserved API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "checktableisreserved".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Check Table Is Reserved API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Check Table Is Reserved API endpoint data from Excel sheet");
            throw new customException("Error While Reading Check Table Is Reserved API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getCheckTableIsReservedData")
    public Object[][] getCheckTableIsReservedData() throws customException {
        try {
            LogUtils.info("Reading check table is reserved test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No check table is reserved test scenario data found in Excel sheet");
                throw new customException("No check table is reserved test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                        "checktableisreserved".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for check table is reserved");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading check table is reserved test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading check table is reserved test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading check table is reserved test scenario data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("Check Table Is Reserved SetUp");
            ExtentReport.createTest("Check Table Is Reserved Setup");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseURI);

            Object[][] checkTableIsReservedUrl = getCheckTableIsReservedUrl();
            if (checkTableIsReservedUrl.length > 0) {
                String endpoint = checkTableIsReservedUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Check Table Is Reserved URL set to: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Check Table Is Reserved URL set to: " + baseURI);
            } else {
                LogUtils.error("No check table is reserved URL found in test data");
                throw new customException("No check table is reserved URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }

            checkTableIsReservedRequest = new TableReservationRequest();
            LogUtils.info("Check Table Is Reserved Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Check Table Is Reserved Setup completed successfully");

        } catch (Exception e) {
            LogUtils.error("Error during check table is reserved setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during check table is reserved setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getCheckTableIsReservedData")
    private void checkIfTableIsReserved(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting check table is reserved test: " + description);
            ExtentReport.createTest("Check Table Is Reserved Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Starting check table is reserved test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseURI);

            if (apiName.equalsIgnoreCase("checktableisreserved") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing check table is reserved request");
                requestBodyJson = new JSONObject(requestBody);
                
                checkTableIsReservedRequest.setTable_id(requestBodyJson.getInt("table_id"));
                checkTableIsReservedRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                checkTableIsReservedRequest.setTable_number(requestBodyJson.getString("table_number"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, checkTableIsReservedRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                    LogUtils.success(logger, "Check table reservation status completed successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Check table reservation status completed successfully", ExtentColor.GREEN));
                    
                    // Just show response without validation
                    if (response.asString() != null && !response.asString().isEmpty()) {
                        actualResponseBody = new JSONObject(response.asString());
                        ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualResponseBody.toString(2));
                    }
                } else {
                    LogUtils.failure(logger, "Check table reservation status failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Check table reservation status failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error during check table is reserved test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during check table is reserved test execution: " + e.getMessage());
        }
    }
    
    /**
     * Validate if a message contains more than the maximum allowed number of sentences
     * @param message The message to validate
     * @param maxSentences Maximum allowed sentences
     * @return Error message if validation fails, null if validation passes
     */
    private String validateSentenceCount(String message, int maxSentences) {
        if (message == null || message.trim().isEmpty()) {
            return null; // Empty message, no sentences
        }
        
        String[] sentences = message.split("[.!?]+");
        int sentenceCount = 0;
        
        for (String sentence : sentences) {
            if (sentence.trim().length() > 0) {
                sentenceCount++;
            }
        }
        
        if (sentenceCount > maxSentences) {
            return "Response message exceeds maximum allowed sentences - Found: " + sentenceCount + 
                   ", Maximum allowed: " + maxSentences;
        }
        
        return null; // Validation passed
    }
}