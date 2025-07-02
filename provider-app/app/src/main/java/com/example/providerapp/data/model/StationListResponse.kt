package com.example.providerapp.data.model

import com.google.gson.annotations.SerializedName

data class StationListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("data")
    val data: List<StationShort>
)