package com.menumitra.apiRequest;

public class UnsplitDeleteRquest {
    private int primary_table_id;
    private int user_id;
    private String app_source;

    public int getPrimary_table_id() {
        return primary_table_id;
    }

    public void setPrimary_table_id(int primary_table_id) {
        this.primary_table_id = primary_table_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getApp_source() {
        return app_source;
    }

    public void setApp_source(String app_source) {
        this.app_source = app_source;
    }
}
