package com.example.providerapp.data.model

import com.google.gson.annotations.SerializedName

data class ValidationErrorDetail(
    @SerializedName("msg")
    val msg: String // Thông báo lỗi cụ thể cho từng trường
)