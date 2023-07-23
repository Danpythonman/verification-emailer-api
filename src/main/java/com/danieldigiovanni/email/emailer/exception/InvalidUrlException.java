package com.danieldigiovanni.email.emailer.exception;

import java.net.URISyntaxException;

/**
 * Exception representing an invalid URL.
 * <p>
 * The purpose of this exception is to convert {@link URISyntaxException}
 * (which is a checked exception) to an unchecked exception to be more
 * consistent with how exceptions are handled in this project.
 */
public class InvalidUrlException extends RuntimeException {

    /**
     * Constructs an InvalidUrlException with the given message.
     *
     * @param message The message of the exception.
     */
    public InvalidUrlException(String message) {
        super(message);
    }

}
