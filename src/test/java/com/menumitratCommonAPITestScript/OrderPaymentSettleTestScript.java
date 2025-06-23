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
import com.menumitra.apiRequest.OrderPaymentSettleTypeRequest;
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
public class OrderPaymentSettleTestScript extends APIBase {
    private OrderPaymentSettleTypeRequest orderPaymentSettleRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private JSONObject expectedJsonBody;
    private Logger logger = LogUtils.getLogger(OrderPaymentSettleTestScript.class);

    @DataProvider(name = "getOrderPaymentSettleUrl")
    public static Object[][] getOrderPaymentSettleUrl() throws customException {
        try {
            LogUtils.info("Reading Order Payment Settle API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            return Arrays.stream(readExcelData)
                    .filter(row -> "orderpaymentsettle".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Order Payment Settle API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Order Payment Settle API endpoint data from Excel sheet");
            throw new customException("Error While Reading Order Payment Settle API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getOrderPaymentSettleData")
    public static Object[][] getOrderPaymentSettleData() throws customException {
        try {
            LogUtils.info("Reading order payment settle test scenario data");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No order payment settle test scenario data found in Excel sheet");
                throw new customException("No order payment settle test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "orderpaymentsettle".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }
            LogUtils.info("Successfully retrieved " + obj.length + " test scenarios for order payment settle");
            return obj;
        } catch (Exception e) {
            LogUtils.error("Error while reading order payment settle test scenario data from Excel sheet: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading order payment settle test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading order payment settle test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for order payment settle test====");
            ExtentReport.createTest("Order Payment Settle Setup");
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);

            Object[][] orderPaymentSettleData = getOrderPaymentSettleUrl();
            if (orderPaymentSettleData.length > 0) {
                String endpoint = orderPaymentSettleData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for order payment settle: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No order payment settle URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No order payment settle URL found in test data");
                throw new customException("No order payment settle URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.error("Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException("Required tokens not found. Please ensure login and OTP verification is completed");
            }

            orderPaymentSettleRequest = new OrderPaymentSettleTypeRequest();
            LogUtils.success(logger, "Order Payment Settle Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Order Payment Settle Setup completed successfully");
        } catch (Exception e) {
            LogUtils.failure(logger, "Error during order payment settle setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during order payment settle setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getOrderPaymentSettleData")
    private void orderPaymentSettle(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBodyPayload, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting order payment settle test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Order Payment Settle Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            ExtentReport.getTest().log(Status.INFO, "Preparing request body");
            LogUtils.info("Preparing request body");

            requestBodyJson = new JSONObject(requestBodyPayload);
            orderPaymentSettleRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
            orderPaymentSettleRequest.setOrder_id(requestBodyJson.getString("order_id"));
            orderPaymentSettleRequest.setPayment_settle_type(requestBodyJson.getString("payment_settle_type"));
            orderPaymentSettleRequest.setUser_id(requestBodyJson.getString("user_id"));

            LogUtils.info("Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
            ExtentReport.getTest().log(Status.INFO, "Making API call to endpoint: " + baseURI);
            LogUtils.info("Making API call to endpoint: " + baseURI);
            ExtentReport.getTest().log(Status.INFO, "Using access token: " + accessToken.substring(0, 15) + "...");
            LogUtils.info("Using access token: " + accessToken.substring(0, 15) + "...");

            response = ResponseUtil.getResponseWithAuth(baseURI, orderPaymentSettleRequest, httpsmethod, accessToken);
            ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
            LogUtils.info("Response Status Code: " + response.getStatusCode());
            ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            LogUtils.info("Response Body: " + response.asPrettyString());

            if (response.getStatusCode() == Integer.parseInt(statusCode) || 
                (response.getStatusCode() == 201 && Integer.parseInt(statusCode) == 200)) {
                ExtentReport.getTest().log(Status.PASS, "Status code validation passed: " + response.getStatusCode());
                LogUtils.success(logger, "Status code validation passed: " + response.getStatusCode());

                if (response.asString() != null && !response.asString().isEmpty()) {
                    actualResponseBody = new JSONObject(response.asString());
                    ExtentReport.getTest().log(Status.INFO, "Response Body: " + actualResponseBody.toString(2));
                }

                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Order payment settled successfully", ExtentColor.GREEN));
                LogUtils.success(logger, "Order payment settled successfully");
            } else {
                String errorMsg = "Status code validation failed - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                LogUtils.failure(logger, errorMsg);
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Test execution failed: " + e.getMessage();
            ExtentReport.getTest().log(Status.FAIL, errorMsg);
            LogUtils.error(errorMsg);
            LogUtils.error("Stack trace: " + Arrays.toString(e.getStackTrace()));

            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body:\n" + response.asPrettyString());
                LogUtils.error("Failed Response Status Code: " + response.getStatusCode());
                LogUtils.error("Failed Response Body:\n" + response.asPrettyString());
            }
            throw new customException(errorMsg);
        }
    }
}
