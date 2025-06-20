package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.google.gson.Gson;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class UnlinkTablesTestScript extends APIBase 
{
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private RequestSpecification request;
    private UnlinkTablesRequest unlinkTablesRequest;
    private Gson gson;
    Logger logger = Logger.getLogger(UnlinkTablesTestScript.class);

    /**
     * Data provider for unlink tables API endpoint URLs
     */
    @DataProvider(name="getUnlinkTablesUrl")
    public Object[][] getUnlinkTablesUrl() throws customException {
        try {
            LogUtils.info("Reading Unlink Tables API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "unlinktables".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Unlink Tables API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Unlink Tables API endpoint data from Excel sheet");
            throw new customException("Error While Reading Unlink Tables API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for unlink tables test scenarios
     */
    @DataProvider(name="getUnlinkTablesData")
    public Object[][] getUnlinkTablesData() throws customException {
        try {
            LogUtils.info("Reading unlink tables test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No unlink tables test scenario data found in Excel sheet");
                throw new customException("No unlink tables test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "unlinktables".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        } catch(Exception e) {
            LogUtils.error("Error while reading unlink tables test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading unlink tables test scenario data: " + e.getMessage());
            throw new customException("Error while reading unlink tables test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Unlink Tables SetUp");
            ExtentReport.createTest("Unlink Tables Setup");
            ActionsMethods.login(); 
            ActionsMethods.verifyOTP();
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            
            Object[][] unlinkTablesUrl = getUnlinkTablesUrl();
            if (unlinkTablesUrl.length > 0) 
            {
                String endpoint = unlinkTablesUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = baseUri+""+url.getPath()+"?"+url.getQuery();
                LogUtils.info("Unlink Tables URL set to: " + baseUri);
            } else {
                LogUtils.error("No unlink tables URL found in test data");
                throw new customException("No unlink tables URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
            gson = new Gson();
            LogUtils.info("Unlink Tables Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during unlink tables setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during unlink tables setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to unlink tables
     */
    @Test(dataProvider="getUnlinkTablesData")
    private void unlinkTablesUsingValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting unlink tables test: " + description);
            ExtentReport.createTest("Unlink Tables Using Valid Input Data");
            ExtentReport.getTest().log(Status.INFO, "Starting unlink tables test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("unlinktables") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing unlink tables request");
                requestBodyJson = new JSONObject(requestBody.replace("\\", "\\\\"));
                expectedResponse = new JSONObject(expectedResponseBody);
               
                // Initialize request body using POJO
                unlinkTablesRequest = new UnlinkTablesRequest(
                    requestBodyJson.getInt("outlet_id"),
                    requestBodyJson.getInt("primary_table_id"),
                    requestBodyJson.getInt("user_id")
                );
                
                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.header("Content-Type", "application/json");

                // Convert POJO to JSON string
                String requestBodyJsonString = gson.toJson(unlinkTablesRequest);
                request.body(requestBodyJsonString);
                
                LogUtils.info("Request Body: " + requestBodyJsonString);
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJsonString);
                
                LogUtils.info("Sending POST request to endpoint: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Sending POST request to unlink tables");
                response = request.when().post(baseUri).then().extract().response();

                LogUtils.info("Received response with status code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Received response with status code: " + response.getStatusCode());
                LogUtils.info("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.INFO, "Response body: " + response.asPrettyString());
                
                if (response.getStatusCode() == Integer.parseInt(statusCode)) 
                {
                    LogUtils.success(logger, "Tables unlinked successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Tables unlinked successfully", ExtentColor.GREEN));
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    LogUtils.info("Response validation completed successfully");
                    ExtentReport.getTest().log(Status.PASS, "Response validation completed successfully");
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
                } 
                else 
                {
                    LogUtils.failure(logger, "Tables unlinking failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Tables unlinking failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                }
            }

        } catch (Exception e) {
            LogUtils.error("Error during unlink tables test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during unlink tables test execution: " + e.getMessage());
        }
    }

    /**
     * Cleanup method to perform post-test cleanup
     */
    @AfterClass
    private void tearDown() throws customException 
    {
        LogUtils.info("Performing tear down");
        ExtentReport.getTest().log(Status.INFO, "Performing tear down");
        ActionsMethods.logout();
        TokenManagers.clearTokens();
    }
} 