package com.example.login.VERIFY_VERIFICATION;

public class VerifyCodeRequest {
    private String email;
    private String providedCode;

    public VerifyCodeRequest(String email, String providedCode) {
        this.email = email;
        this.providedCode = providedCode;
    }
}