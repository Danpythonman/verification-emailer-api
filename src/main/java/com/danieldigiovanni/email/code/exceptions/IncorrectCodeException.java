package com.danieldigiovanni.email.code.exceptions;

/**
 * Exception representing the case where the verification code that the user
 * sent is incorrect (does not match the code that was actually  created).
 */
public class IncorrectCodeException extends RuntimeException {

    /**
     * Constructs an InvalidCodeException exception with the exception message
     * as "Incorrect code provided".
     */
    public IncorrectCodeException() {
        super("Incorrect code provided");
    }

}
