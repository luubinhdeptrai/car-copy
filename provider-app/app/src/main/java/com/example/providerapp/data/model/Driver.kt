// file: data/model/Driver.kt
package com.example.providerapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Driver(
    @SerializedName("_id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("currentStation")
    val currentStation: StationShort,

    @SerializedName("photo")
    val photoUrl: String?
) : Parcelable