package com.example.lmchecker.dto;

public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresInMs;
    private UserDto user;

    public LoginResponse() {}

    public LoginResponse(String accessToken, long expiresInMs, UserDto user) {
        this.accessToken = accessToken;
        this.expiresInMs = expiresInMs;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresInMs() { return expiresInMs; }
    public void setExpiresInMs(long expiresInMs) { this.expiresInMs = expiresInMs; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
}
