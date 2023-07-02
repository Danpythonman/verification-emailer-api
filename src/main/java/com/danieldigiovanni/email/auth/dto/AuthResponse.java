package com.danieldigiovanni.email.auth.dto;

/**
 * Format of the response body for response to login and register requests.
 */
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
