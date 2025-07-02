package com.example.providerapp.data.model

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("currentPage")
    val currentPage: Int
)