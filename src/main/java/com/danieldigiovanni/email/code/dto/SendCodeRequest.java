package com.danieldigiovanni.email.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Format of the request body for sending a verification code.
 */
public class SendCodeRequest {

    @NotNull
    @Email
    private String email;
    @Min(2)
    @Max(10)
    private Integer length = 6;
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

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

}
