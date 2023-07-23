package com.danieldigiovanni.email.emailer.exception;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Exception representing failed API call due to an unexpected response body.
 * <p>
 * This means an API was called and returned an acceptable status, but the
 * response body did not match what we expected.
 */
public class ApiCallResponseBodyException extends RuntimeException {

    private final JsonNode responseBody;

    /**
     * Constructs an ApiCallStatusException with the given message, and the API
     * call response's body.
     *
     * @param message      The message of the exception.
     * @param responseBody The response body returned from the API call.
     */
    public ApiCallResponseBodyException(String message, JsonNode responseBody) {
        super(message);
        this.responseBody = responseBody;
    }

}
