package com.danieldigiovanni.email.code.dto;

import com.danieldigiovanni.email.code.Code;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Format of the response body for sending a verification code.
 */
public class CodeResponse {

    private Date createdAt;
    private Date expiresAt;
    private Integer maximumAttempts;
    private Integer remainingAttempts;

    public CodeResponse() { }

    public CodeResponse(Code code) {
        this.createdAt = code.getCreatedAt();
        this.expiresAt = this.calculateExpiry(code);
        this.maximumAttempts = code.getMaximumAttempts();
        this.remainingAttempts = code.getMaximumAttempts()
            - code.getIncorrectAttempts();
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpiresAt() {
        return this.expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getMaximumAttempts() {
        return this.maximumAttempts;
    }

    public void setMaximumAttempts(Integer maximumAttempts) {
        this.maximumAttempts = maximumAttempts;
    }

    public Integer getRemainingAttempts() {
        return this.remainingAttempts;
    }

    public void setRemainingAttempts(Integer remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }

    /**
     * Calculates the date at which the given code will expire.
     *
     * @param code The code whose expiry is being calculated.
     *
     * @return The code's expiry date.
     */
    private Date calculateExpiry(Code code) {
        long startTimeInMillis = code.getCreatedAt().getTime();
        long maximumDurationInMillis = TimeUnit.MINUTES.toMillis(
            code.getMaximumDurationInMinutes()
        );
        return new Date(startTimeInMillis + maximumDurationInMillis);
    }

}
