package com.approvalApp.approvalApp.dto;

public class AuthResponse {
    private String userId;
    private String token;

    public AuthResponse(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}

