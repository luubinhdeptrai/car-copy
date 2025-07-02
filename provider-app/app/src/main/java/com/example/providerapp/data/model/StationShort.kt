package com.example.providerapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize // THÊM DÒNG NÀY
data class StationShort(
    @SerializedName("_id")
    val id: String,

    @SerializedName("name")
    val name: String
) : Parcelable // VÀ THÊM KẾ THỪA NÀY