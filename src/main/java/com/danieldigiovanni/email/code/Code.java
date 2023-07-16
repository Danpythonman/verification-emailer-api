package com.danieldigiovanni.email.code;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Entity
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String hash;
    @NotNull
    private Date createdAt;
    private Date fulfilledAt;
    @NotNull
    private Integer incorrectAttempts = 0;
    @NotNull
    private Integer maximumAttempts;
    @NotNull
    private Integer maximumDurationInMinutes;

    public Code() { }

    private Code(CodeBuilder codeBuilder) {
        this.email = codeBuilder.email;
        this.hash = codeBuilder.hash;
        this.createdAt = codeBuilder.createdAt;
        this.maximumAttempts = codeBuilder.maximumAttempts;
        this.maximumDurationInMinutes = codeBuilder.maximumDurationInMinutes;
    }

    public static CodeBuilder builder() {
        return new CodeBuilder();
    }

    /**
     * Checks if the code is active.
     * <p>
     * The code is active if its time limit and
     * maximum number of incorrect attempts have both not been reached.
     *
     * @return True if the code is not expired and the maximum number of
     * incorrect attempts has not been reached.
     */
    public boolean isActive() {
        boolean codeIsExpired = TimeUnit.MILLISECONDS.toMinutes(
            new Date().getTime() - this.createdAt.getTime()
        ) >= this.maximumDurationInMinutes;

        boolean maximumAttemptsReached
            = this.getIncorrectAttempts() >= this.maximumAttempts;

        return !codeIsExpired && !maximumAttemptsReached;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getFulfilledAt() {
        return this.fulfilledAt;
    }

    public void setFulfilledAt(Date fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
    }

    public Integer getIncorrectAttempts() {
        return this.incorrectAttempts;
    }

    public void setIncorrectAttempts(Integer incorrectAttempts) {
        this.incorrectAttempts = incorrectAttempts;
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

    public static class CodeBuilder {
        private String email;
        private String hash;
        private Date createdAt;
        private Integer maximumAttempts;
        private Integer maximumDurationInMinutes;

        public CodeBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CodeBuilder hash(String hash) {
            this.hash = hash;
            return this;
        }

        public CodeBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CodeBuilder maximumAttempts(Integer maximumAttempts) {
            this.maximumAttempts = maximumAttempts;
            return this;
        }

        public CodeBuilder maximumDurationInMinutes(Integer maximumDurationInMinutes) {
            this.maximumDurationInMinutes = maximumDurationInMinutes;
            return this;
        }

        public Code build() {
            return new Code(this);
        }
    }

}
