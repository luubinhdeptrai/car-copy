// File: com/example/login/MODELS/IssueResponse.java
package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class IssueResponse implements Serializable {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Issue data; // Có thể trả về đối tượng Issue đã tạo/cập nhật

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Issue getData() {
        return data;
    }
}