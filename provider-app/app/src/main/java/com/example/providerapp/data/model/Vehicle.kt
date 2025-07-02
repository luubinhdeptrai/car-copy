package com.example.providerapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vehicle(
    @SerializedName("_id")
    val id: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("licensePlate")
    val licensePlate: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("capacity")
    val capacity: Int,

    @SerializedName("currentStation")
    val currentStation: StationShort,

    @SerializedName("image")
    val imageUrl: String?
) : Parcelable