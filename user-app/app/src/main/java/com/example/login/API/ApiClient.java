package com.example.login.API;

import android.content.Context;
import java.util.concurrent.TimeUnit; // Import này cần thiết cho TimeUnit
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofitAuth = null; // Dùng biến riêng cho Auth API
    private static Retrofit retrofitNoAuth = null; // Dùng biến riêng cho NoAuth API
    
    private static final String BASE_URL = "https://limogo-backend.onrender.com/"; // Đảm bảo đúng IP và Port
//    private static final String BASE_URL = "http://10.0.2.2:3000/"; // Đảm bảo đúng IP và Port

     
    public static ApiService getAuthAPI(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new AuthInterceptor(context)) // Thêm interceptor để đính kèm token
                // Cấu hình Timeout cho kết nối có chứng thực
                .connectTimeout(90, TimeUnit.SECONDS) // Thời gian chờ kết nối
                .readTimeout(90, TimeUnit.SECONDS)    // Thời gian chờ đọc dữ liệu
                .writeTimeout(90, TimeUnit.SECONDS)   // Thời gian chờ ghi dữ liệu
                .build();

        // Chỉ khởi tạo lại Retrofit nếu cần (ví dụ: client thay đổi)
        // Cập nhật điều kiện kiểm tra để so sánh client object
        if (retrofitAuth == null || retrofitAuth.baseUrl().toString().equals(BASE_URL) == false || retrofitAuth.callFactory() != client) {
            retrofitAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitAuth.create(ApiService.class);
    }

    public static ApiService getNoAuthAPI() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                // Cấu hình Timeout cho kết nối không chứng thực
                .connectTimeout(90, TimeUnit.SECONDS) // Thời gian chờ kết nối
                .readTimeout(90, TimeUnit.SECONDS)    // Thời gian chờ đọc dữ liệu
                .writeTimeout(90, TimeUnit.SECONDS)   // Thời gian chờ ghi dữ liệu
                .build();

        // Chỉ khởi tạo lại Retrofit nếu cần
        // Cập nhật điều kiện kiểm tra để so sánh client object
        if (retrofitNoAuth == null || retrofitNoAuth.baseUrl().toString().equals(BASE_URL) == false || retrofitNoAuth.callFactory() != client) {
            retrofitNoAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitNoAuth.create(ApiService.class);
    }
}