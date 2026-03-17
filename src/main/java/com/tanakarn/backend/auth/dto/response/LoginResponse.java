package com.tanakarn.backend.auth.dto.response;

public class LoginResponse {
    private String token;
    private String username;
    private long accountId;

    public String getToken() {
        return token;
    }
    public String getUsername() {
        return username;
    }
    public Long getAccountId() {
        return accountId;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
