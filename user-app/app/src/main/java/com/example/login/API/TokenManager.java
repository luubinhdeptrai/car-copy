package com.example.login.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log; // Thêm Log để debug

public class TokenManager {
    private static final String PREF_NAME = "lmg_prefs";
    private static final String KEY_ACCESS_TOKEN = "jwt";

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        Log.d("TokenManager", "Token saved: " + token); // Log để kiểm tra
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);
        Log.d("TokenManager", "Token retrieved: " + (token != null ? "exists" : "null")); // Log để kiểm tra
        return token;
    }

    public static void deleteToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.apply();
        Log.d("TokenManager", "Token deleted."); // Log để kiểm tra
    }
}