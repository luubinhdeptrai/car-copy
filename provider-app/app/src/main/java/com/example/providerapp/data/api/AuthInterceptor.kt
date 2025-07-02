// file: data/api/AuthInterceptor.kt
package com.example.providerapp.data.api

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // TẠM THỜI: Gắn cứng token để test
        val hardcodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2ODVkNTYxNjZhNmZlODhiZWZkMzdlZjMiLCJlbWFpbCI6InByb3ZpZGVyLmZ1dGFAbGltb2dvLmNvbSIsInJvbGUiOiJwcm92aWRlciIsInBob25lTnVtYmVyIjoiKzg0MjIyMjIyMjIyIiwidmVyaWZpZWQiOnRydWUsImlhdCI6MTc1MTQ0NjUwMSwiZXhwIjoxNzUxNDc1MzAxfQ.1axi2mrF2f5LoZatf_d8GhRH4V-AJVS9oClBVwx8i24"

        requestBuilder.addHeader("Authorization", "Bearer $hardcodedToken")

        return chain.proceed(requestBuilder.build())
    }
}