// file: data/api/AuthInterceptor.kt
package com.example.providerapp.data.api

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // TẠM THỜI: Gắn cứng token để test
        val hardcodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2ODY0ZWE1YjFjOWY4MjljZTM3NWU2MTMiLCJlbWFpbCI6InByb3ZpZGVyLmZ1dGFAbGltb2dvLmNvbSIsInJvbGUiOiJwcm92aWRlciIsInBob25lTnVtYmVyIjoiKzg0MjIyMjIyMjIyIiwidmVyaWZpZWQiOnRydWUsImlhdCI6MTc1MTQ1NTI2NSwiZXhwIjoxNzUxNDg0MDY1fQ.lTgNI03823Qtq1KU7FTh9_C1k4J3EFHiF1glXXxo7Qw"

        requestBuilder.addHeader("Authorization", "Bearer $hardcodedToken")

        return chain.proceed(requestBuilder.build())
    }
}