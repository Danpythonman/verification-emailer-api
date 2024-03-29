package com.danieldigiovanni.email.code;

import com.danieldigiovanni.email.customer.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Entity
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;
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
        this.customer = codeBuilder.customer;
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
     * The code is active if it:
     * <ul>
     *     <li>is not expired (has not reached maximum time limit),</li>
     *     <li>has not reached the maximum number of attempts,</li>
     *     <li>and is not already fulfilled.</li>
     * </ul>
     *
     * @return True if the code is active.
     */
    public boolean isActive() {
        boolean codeIsExpired = TimeUnit.MILLISECONDS.toMinutes(
            new Date().getTime() - this.createdAt.getTime()
        ) >= this.maximumDurationInMinutes;

        boolean maximumAttemptsReached
            = this.getIncorrectAttempts() >= this.maximumAttempts;

        boolean codeIsFulFilled = this.fulfilledAt != null;

        return !codeIsFulFilled && !codeIsExpired && !maximumAttemptsReached;
    }

    /**
     * Increments the number of incorrect attempts by one.
     */
    public void incrementIncorrectAttempts() {
        this.incorrectAttempts++;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

        private Customer customer;
        private String email;
        private String hash;
        private Date createdAt;
        private Integer maximumAttempts;
        private Integer maximumDurationInMinutes;

        public CodeBuilder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

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
