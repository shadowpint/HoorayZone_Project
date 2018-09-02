package com.horrayzone.horrayzone.model;

import com.google.gson.annotations.SerializedName;

public class UserInfo {

    @SerializedName("id")
    private String id;
    @SerializedName("username")
    private String username;


    @SerializedName("firstname")
    private String firstName;

    @SerializedName("lastname")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("companyname")
    private String company;

    @SerializedName("status")
    private String status;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }

    public String getStatus() {
        return status;
    }
}
