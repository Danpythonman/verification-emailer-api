package com.danieldigiovanni.email.emailer;

/**
 * Sends email.
 * <p>
 * This can be sending an actual email for production, or some other operation
 * for testing.
 */
public interface Emailer {

    /**
     * Sends an email.
     *
     * @param toAddress The recipient of the email.
     * @param subject   The subject of the email.
     * @param code      The verification code.
     * @param duration  The duration of the verification code in minutes.
     */
    void sendEmail(String toAddress, String subject, String code, Integer duration);

}
