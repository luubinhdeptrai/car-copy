// file: data/model/DriverListResponse.kt
package com.example.providerapp.data.model

import com.google.gson.annotations.SerializedName

// Lớp này có thể được tái sử dụng bằng Generic nếu muốn, nhưng tạo riêng cho rõ ràng
data class DriverListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("data")
    val data: List<Driver>,

    @SerializedName("pagination")
    val pagination: Pagination
)