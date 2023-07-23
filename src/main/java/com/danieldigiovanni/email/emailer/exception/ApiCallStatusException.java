package com.danieldigiovanni.email.emailer.exception;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatusCode;

/**
 * Exception representing failed API call due to error status.
 * <p>
 * This means an API was called and returned a status that we did not expect.
 */
public class ApiCallStatusException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final JsonNode responseBody;

    /**
     * Constructs an ApiCallStatusException with the given message, and the API
     * call response's status and body.
     *
     * @param message      The message of the exception.
     * @param statusCode   The status returned from the API call.
     * @param responseBody The response body returned from the API call.
     */
    public ApiCallStatusException(String message, HttpStatusCode statusCode, JsonNode responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

}
