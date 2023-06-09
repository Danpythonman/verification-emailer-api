package com.danieldigiovanni.email.auth;

public class AuthResponse {

    private String token;

    public AuthResponse() { }

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public String setToken(String token) {
        return this.token = token;
    }

}
