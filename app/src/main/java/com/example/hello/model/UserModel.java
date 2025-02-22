package com.example.hello.model;

import com.google.firebase.Timestamp;

public class UserModel {

    private String phone;
    private String username;
    private Timestamp createTimestamp;
    private String userId;

    public UserModel() {
    }

    public UserModel(String phone, String username, Timestamp createTimestamp,String userId) {
        this.phone = phone;
        this.username = username;
        this.createTimestamp = createTimestamp;
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
