package com.danieldigiovanni.email.code.dto;

import jakarta.validation.constraints.*;

/**
 * Format of the request body for sending a custom verification code.
 */
public class SendCustomCodeRequest {

    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 2, max = 64)
    private String code;
    @Min(1)
    private Integer maximumAttempts = 5;
    @Min(1)
    @Max(10)
    private Integer maximumDurationInMinutes = 5;

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

    public Integer getMaximumAttempts() {
        return this.maximumAttempts;
    }

    public void setMaximumAttempts(Integer maximumAttempts) {
        this.maximumAttempts = maximumAttempts;
    }

    public Integer getMaximumDurationInMinutes() {
        return this.maximumDurationInMinutes;
    }

    public void setMaximumDurationInMinutes(Integer maximumDurationInMinutes) {
        this.maximumDurationInMinutes = maximumDurationInMinutes;
    }

}
