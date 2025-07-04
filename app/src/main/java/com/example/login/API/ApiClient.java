package com.example.login.API;

import android.content.Context;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofitAuth = null;
    private static Retrofit retrofitNoAuth = null; // Biến này sẽ được dùng cho NoAuth API

    // Base URL của Backend. Đảm bảo đây là địa chỉ đúng của server.
    // Nếu chạy trên emulator và backend là localhost:3000, dùng http://10.0.2.2:3000/
    // Nếu deploy lên Render, dùng URL của Render.
    private static final String BASE_URL = "https://limogo-backend.onrender.com/";
    // private static final String BASE_URL = "http://10.0.2.2:3000/";


    // --- Instance của OkHttpClient cho API có chứng thực ---
    private static OkHttpClient authClient = null;
    // --- Instance của OkHttpClient cho API không chứng thực ---
    private static OkHttpClient noAuthClient = null;


    // Phương thức để lấy API Service CÓ chứng thực (cần Context cho AuthInterceptor)
    public static ApiService getAuthAPI(Context context) {
        if (authClient == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            authClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(new AuthInterceptor(context)) // AuthInterceptor cần Context
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .build();
        }

        // Khởi tạo RetrofitAuth chỉ một lần
        if (retrofitAuth == null || !retrofitAuth.baseUrl().toString().equals(BASE_URL) || retrofitAuth.callFactory() != authClient) {
            retrofitAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(authClient) // Sử dụng authClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitAuth.create(ApiService.class);
    }

    // Phương thức để lấy API Service KHÔNG chứng thực
    public static ApiService getNoAuthAPI() {
        if (noAuthClient == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            noAuthClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    // NoAuth API không cần AuthInterceptor
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .build();
        }

        // Khởi tạo RetrofitNoAuth chỉ một lần
        if (retrofitNoAuth == null || !retrofitNoAuth.baseUrl().toString().equals(BASE_URL) || retrofitNoAuth.callFactory() != noAuthClient) {
            retrofitNoAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(noAuthClient) // Sử dụng noAuthClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitNoAuth.create(ApiService.class);
    }

    // Phương thức mà SearchTripFragment sẽ gọi (tên mới để dễ hiểu)
    public static ApiService getPublicApiService() {
        return getNoAuthAPI();
    }
}