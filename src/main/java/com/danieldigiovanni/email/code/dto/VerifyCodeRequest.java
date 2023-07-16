package com.danieldigiovanni.email.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class VerifyCodeRequest {

    @NotNull
    @Email
    private String email;
    @NotNull
    private String code;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
