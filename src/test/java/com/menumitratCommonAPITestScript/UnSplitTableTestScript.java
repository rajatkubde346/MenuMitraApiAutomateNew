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
import com.menumitra.apiRequest.UnsplitDeleteRquest;
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

import io.restassured.response.Response;

@Listeners(Listener.class)
public class UnSplitTableTestScript extends APIBase {
    private UnsplitDeleteRquest unsplitRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private Logger logger = LogUtils.getLogger(UnSplitTableTestScript.class);

    @DataProvider(name = "getUnsplitTableUrl")
    public static Object[][] getUnsplitTableUrl() throws customException {
        try {
            LogUtils.info("Reading Unsplit Table API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            return Arrays.stream(readExcelData)
                    .filter(row -> "unsplittable".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Unsplit Table API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Unsplit Table API endpoint data from Excel sheet");
            throw new customException("Error While Reading Unsplit Table API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getUnsplitTableData")
    public static Object[][] getUnsplitTableData() throws customException {
        try {
            LogUtils.info("Reading unsplit table test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No unsplit table test scenario data found in Excel sheet");
                throw new customException("No unsplit table test scenario data found in Excel sheet");
            }
            List<Object[]> filteredData = new ArrayList<>();
            for (Object[] row : readExcelData) {
                if (row != null && row.length >= 3 &&
                        "unsplittable".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }
            return filteredData.toArray(new Object[0][]);
        } catch (Exception e) {
            LogUtils.error("Error while reading unsplit table test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading unsplit table test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading unsplit table test scenario data: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for unsplit table test====");
            ExtentReport.createTest("Unsplit Table Setup");
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
            Object[][] unsplitTableData = getUnsplitTableUrl();
            if (unsplitTableData.length > 0) {
                String endpoint = unsplitTableData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for unsplit table: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No unsplit table URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No unsplit table URL found in test data");
                throw new customException("No unsplit table URL found in test data");
            }
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }
            unsplitRequest = new UnsplitDeleteRquest();
            LogUtils.success(logger, "Unsplit Table Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Unsplit Table Setup completed successfully");
        } catch (Exception e) {
            LogUtils.failure(logger, "Error during unsplit table setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during unsplit table setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getUnsplitTableData")
    private void unsplitTable(String apiName, String testCaseId, String testType, String description,
            String httpsMethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting unsplit table test case: " + testCaseId);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Unsplit Table Test - " + testCaseId);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");
            
            requestBodyJson = new JSONObject(requestBodyPayload);
            unsplitRequest.setPrimary_table_id(requestBodyJson.getInt("primary_table_id"));
            unsplitRequest.setUser_id(requestBodyJson.getInt("user_id"));
            unsplitRequest.setApp_source(requestBodyJson.getString("app_source"));
            
            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            
            response = ResponseUtil.getResponseWithAuth(baseURI, unsplitRequest, httpsMethod, accessToken);
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            if (response.getStatusCode() == Integer.parseInt(statusCode)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());
                if (response.asString() != null && !response.asString().isEmpty()) {
                    actualResponseBody = new JSONObject(response.asString());
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualResponseBody.toString(2));
                }
                ExtentReport.getTest().log(Status.PASS, 
                    MarkupHelper.createLabel("Table unsplit successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "Table unsplit successfully");
            } else {
                String errorMessage = "Status code validation failed - Expected: " + statusCode + 
                                    ", Actual: " + response.getStatusCode();
                LogUtils.failure(logger, errorMessage);
                ExtentReport.getTest().log(Status.FAIL, errorMessage);
                throw new customException("Test execution failed: " + errorMessage);
            }

        } catch (Exception e) {
            LogUtils.error("Test execution failed: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Test execution failed: " + e.getMessage());
            throw new customException("Test execution failed: " + e.getMessage());
        }
    }
}
