package com.example.providerapp.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    // Thêm trường 'errors' để hứng các lỗi validation chi tiết
    // Kiểu dữ liệu là một Map, với key là tên trường (vd: "name")
    // và value là đối tượng chứa thông báo lỗi.
    // Dùng nullable (?) vì không phải lúc nào cũng có lỗi validation.
    @SerializedName("errors")
    val errors: Map<String, ValidationErrorDetail>?
)