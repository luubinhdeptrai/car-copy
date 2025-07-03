// File: com/example/login/MODELS/CreateIssueRequest.java
package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class CreateIssueRequest {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private String type;

    public CreateIssueRequest(String title, String description, String type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }

    // Getters (Optional, but good practice)
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }
}