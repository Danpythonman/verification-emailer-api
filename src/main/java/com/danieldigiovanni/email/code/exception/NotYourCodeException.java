package com.danieldigiovanni.email.code.exception;

/**
 * Exception representing the case where a user tried to verify a code that
 * they did not send (a.k.a. tried to verify another user's code).
 */
public class NotYourCodeException extends RuntimeException {

    /**
     * Constructs an empty NotYourCodeException.
     * <p>
     * We don't need a message or cause because this is such a specific case.
     * The exception handler can handle all that on its own.
     */
    public NotYourCodeException() {
        super();
    }

}
