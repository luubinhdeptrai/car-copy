package com.example.login.SIGNUP;

public class SignupRequest {
    private String email;
    private String password;
    private String phoneNumber;

    public SignupRequest(String email, String password, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}