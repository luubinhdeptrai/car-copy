package com.example.providerapp.data.model

import com.google.gson.annotations.SerializedName

data class VehicleListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<Vehicle>,

    @SerializedName("pagination")
    val pagination: Pagination
)