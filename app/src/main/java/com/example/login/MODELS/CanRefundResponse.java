package com.example.login.MODELS;

import com.google.gson.annotations.SerializedName;

public class CanRefundResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("canRefund")
    private boolean canRefund;

    @SerializedName("reason")
    private String reason;



    public boolean isSuccess() {
        return success;
    }

    public boolean isCanRefund() {
        return canRefund;
    }
    public String getReason() {
        return reason;
    }



}
