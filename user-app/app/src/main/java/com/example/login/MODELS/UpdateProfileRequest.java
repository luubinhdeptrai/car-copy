package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("dateOfBirth")
    private String dateOfBirth; // Định dạng yyyy-MM-dd

    @SerializedName("gender")
    private String gender;

    public UpdateProfileRequest(String name, String dateOfBirth, String gender) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }
}