package com.danieldigiovanni.email.auth;

import jakarta.validation.constraints.NotNull;

/**
 * Format of the request body for login requests.
 */
public class LoginRequest {

    @NotNull
    private String email;
    @NotNull
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
