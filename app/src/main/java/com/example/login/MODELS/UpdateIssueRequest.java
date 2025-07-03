// File: com/example/login/MODELS/UpdateIssueRequest.java
package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class UpdateIssueRequest {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private String type;

    @SerializedName("status") // Có thể cập nhật trạng thái nếu cần
    private String status;

    public UpdateIssueRequest(String title, String description, String type, String status) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.status = status;
    }

    // Constructor chỉ cập nhật title, description, type (nếu không muốn cập nhật status)
    public UpdateIssueRequest(String title, String description, String type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }

    // Getters (Optional)
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
}