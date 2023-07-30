package com.danieldigiovanni.email.metrics;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
public class Metrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long customerId;
    @NotNull
    private Date requestDate;
    @NotNull
    private String requestMethod;
    @NotNull
    private String requestURI;

    public Metrics() { }

    public Metrics(MetricsBuilder metricsBuilder) {
        this.customerId = metricsBuilder.customerId;
        this.requestDate = metricsBuilder.requestDate;
        this.requestMethod = metricsBuilder.requestMethod;
        this.requestURI = metricsBuilder.requestURI;
    }

    public static MetricsBuilder builder() {
        return new MetricsBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Date getRequestDate() {
        return this.requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestURI() {
        return this.requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public static class MetricsBuilder {

        private Long customerId;
        private Date requestDate;
        private String requestMethod;
        private String requestURI;

        public MetricsBuilder setCustomerId(Long customerId) {
            this.customerId = customerId;
            return this;
        }

        public MetricsBuilder setRequestDate(Date requestDate) {
            this.requestDate = requestDate;
            return this;
        }

        public MetricsBuilder setRequestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public MetricsBuilder setRequestURI(String requestURI) {
            this.requestURI = requestURI;
            return this;
        }

        public Metrics build() {
            return new Metrics(this);
        }

    }

}
