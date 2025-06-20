package com.menumitra.utilityclass;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateExcelData {
    private static final String EXCEL_PATH = "src/test/resources/excelsheet/apiEndpoint.xlsx";

    public static void updateOwnerDetailsData() {
        try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Update ownerAPI sheet
            Sheet ownerApiSheet = workbook.getSheet("ownerAPI");
            if (ownerApiSheet == null) {
                ownerApiSheet = workbook.createSheet("ownerAPI");
            }

            // Add header row if not exists
            if (ownerApiSheet.getRow(0) == null) {
                Row headerRow = ownerApiSheet.createRow(0);
                headerRow.createCell(0).setCellValue("API Name");
                headerRow.createCell(1).setCellValue("Description");
                headerRow.createCell(2).setCellValue("Endpoint");
            }

            // Add owner details data
            Row dataRow = ownerApiSheet.createRow(1);
            dataRow.createCell(0).setCellValue("ownerDetails");
            dataRow.createCell(1).setCellValue("Get Owner Details API");
            dataRow.createCell(2).setCellValue("/v2/owner/details");

            // Update OwnerAPITestScenario sheet
            Sheet testScenarioSheet = workbook.getSheet("OwnerAPITestScenario");
            if (testScenarioSheet == null) {
                testScenarioSheet = workbook.createSheet("OwnerAPITestScenario");
            }

            // Add header row if not exists
            if (testScenarioSheet.getRow(0) == null) {
                Row headerRow = testScenarioSheet.createRow(0);
                headerRow.createCell(0).setCellValue("API Name");
                headerRow.createCell(1).setCellValue("Test Case ID");
                headerRow.createCell(2).setCellValue("Test Type");
                headerRow.createCell(3).setCellValue("Description");
                headerRow.createCell(4).setCellValue("HTTP Method");
                headerRow.createCell(5).setCellValue("Request Body");
                headerRow.createCell(6).setCellValue("Expected Response");
                headerRow.createCell(7).setCellValue("Status Code");
            }

            // Add test scenario data
            Row testRow = testScenarioSheet.createRow(1);
            testRow.createCell(0).setCellValue("ownerDetails");
            testRow.createCell(1).setCellValue("TC_001");
            testRow.createCell(2).setCellValue("positive");
            testRow.createCell(3).setCellValue("Get owner details with valid token");
            testRow.createCell(4).setCellValue("GET");
            testRow.createCell(5).setCellValue("{}");
            testRow.createCell(6).setCellValue("{\"status\":\"success\"}");
            testRow.createCell(7).setCellValue("200");

            // Save the workbook
            try (FileOutputStream fos = new FileOutputStream(EXCEL_PATH)) {
                workbook.write(fos);
            }

            System.out.println("Excel file updated successfully with owner details data");

        } catch (IOException e) {
            System.err.println("Error updating Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        updateOwnerDetailsData();
    }
} 