package com.danieldigiovanni.email.emailer.dto;

/**
 * Request body for calling the send mail API.
 */
public class ApiEmailerRequest {

    private String fromAddress;
    private String toAddress;
    private String subject;
    private String content;

    public ApiEmailerRequest(String fromAddress, String toAddress, String subject, String content) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.subject = subject;
        this.content = content;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return this.toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
