package com.danieldigiovanni.email.emailer.exception;

/**
 * Exception representing failed email sending to Mailtrap.
 */
public class MailtrapEmailerException extends RuntimeException {

    /**
     * Constructs a MailTrapEmailerException with the given cause.
     *
     * @param cause The message of the exception.
     */
    public MailtrapEmailerException(Throwable cause) {
        super(cause);
    }

}
