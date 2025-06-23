package com.menumitra.apiRequest;

public class OrderPaymentSettleTypeRequest {
    private String outlet_id;
    private String order_id;
    private String payment_settle_type;
    private String user_id;

    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPayment_settle_type() {
        return payment_settle_type;
    }

    public void setPayment_settle_type(String payment_settle_type) {
        this.payment_settle_type = payment_settle_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
