// File: com/example/login/MODELS/IssueListResponse.java
package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class IssueListResponse implements Serializable {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Issue> data; // Danh sách các đối tượng Issue

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Issue> getData() {
        return data;
    }
}