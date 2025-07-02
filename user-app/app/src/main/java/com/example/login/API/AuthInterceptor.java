package com.example.login.API;

import android.content.Context;
import android.util.Log; // Thêm Log để debug
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = TokenManager.getToken(context); // Lấy token từ TokenManager

        if (token != null && !token.isEmpty()) {
            Request.Builder builder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token);
            Log.d("AuthInterceptor", "Adding Authorization header with token: " + token.substring(0, Math.min(token.length(), 10)) + "..."); // Log để kiểm tra token
            originalRequest = builder.build();
        } else {
            Log.w("AuthInterceptor", "No token found, not adding Authorization header."); // Log nếu không có token
        }
        return chain.proceed(originalRequest);
    }
}