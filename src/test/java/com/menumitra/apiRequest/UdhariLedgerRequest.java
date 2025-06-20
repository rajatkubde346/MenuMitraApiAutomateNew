package com.menumitra.apiRequest;

public class UdhariLedgerRequest {
    private Long userId;
    private String customerName;
    private String customerMobile;
    private String customerAddress;
    private Long orderId;
    private Double billAmount;
    private String estimatedSettlementPeriod;
    private String app_source;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Double billAmount) {
        this.billAmount = billAmount;
    }

    public String getEstimatedSettlementPeriod() {
        return estimatedSettlementPeriod;
    }

    public void setEstimatedSettlementPeriod(String estimatedSettlementPeriod) {
        this.estimatedSettlementPeriod = estimatedSettlementPeriod;
    }

    public String getApp_source() {
        return app_source;
    }

    public void setApp_source(String app_source) {
        this.app_source = app_source;
    }
}
