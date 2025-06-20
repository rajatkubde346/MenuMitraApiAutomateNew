package com.menumitra;

public class SplitTableRequest {
    
    private String outlet_id;
    private String section_id;
    private int primary_table_id;
    private int split_count;
    private int user_id;
    private String app_source;

    public String getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(String outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public int getPrimary_table_id() {
        return primary_table_id;
    }

    public void setPrimary_table_id(int primary_table_id) {
        this.primary_table_id = primary_table_id;
    }

    public int getSplit_count() {
        return split_count;
    }

    public void setSplit_count(int split_count) {
        this.split_count = split_count;
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
