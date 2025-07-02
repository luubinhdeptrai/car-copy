package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("name")
    private String fullname;
    @SerializedName("email")
    private String email;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("userRole")
    private String userRole;
    @SerializedName("verified")
    private boolean verified;
    @SerializedName("_id")
    private String id;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;

    // Thuộc tính mới được thêm vào
    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    // Thuộc tính mới được thêm vào
    @SerializedName("gender")
    private String gender;

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserRole() {
        return userRole;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    // Getter và Setter cho dateOfBirth
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // Getter và Setter cho gender
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}