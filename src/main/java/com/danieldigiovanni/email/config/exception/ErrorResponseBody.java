package com.danieldigiovanni.email.config.exception;

/**
 * The response body format for all errors.
 * <p>
 * The attributes are:
 * <ul>
 *     <li><b>error</b> - a name for the error</li>
 *     <li><b>message</b> - a more detailed error message</li>
 *     <li>
 *         <b>handled</b> - true if this error was caught and handled by an
 *         exception handler (in the class {@link ControllerExceptionHandler}),
 *         false otherwise.
 *     </li>
 * </ul>
 */
public class ErrorResponseBody {

    private final String error;
    private final String message;
    private final Boolean handled;

    public ErrorResponseBody(String error, String message, Boolean handled) {
        this.error = error;
        this.message = message;
        this.handled = handled;
    }

    /**
     * Creates a response body for a handled error response. This means the
     * {@code isHandled} property will be set to {@code true}.
     * <p>
     * Handled means that the exception was caught and handled by an exception
     * handler in {@link ControllerExceptionHandler}, and <b>not</b> propagated
     * to {@link DefaultErrorController}.
     *
     * @param error   A name for the error.
     * @param message A detailed message for the error.
     *
     * @return The response body for the handled error.
     */
    public static ErrorResponseBody handledErrorResponse(String error, String message) {
        return new ErrorResponseBody(error, message, true);
    }

    /**
     * Creates a response body for an unhandled error response. This means the
     * {@code isHandled} property will be set to {@code false}.
     * <p>
     * Unhandled means that the exception was <b>not</b> caught (and <b>not</b>
     * handled) by an exception handler in {@link ControllerExceptionHandler},
     * and was instead propagated to {@link DefaultErrorController}.
     *
     * @param error   A name for the error.
     * @param message A detailed message for the error.
     *
     * @return The response body for the unhandled error.
     */
    public static ErrorResponseBody unHandledErrorResponse(String error, String message) {
        return new ErrorResponseBody(error, message, false);
    }

    public String getError() {
        return this.error;
    }

    public String getMessage() {
        return this.message;
    }

    public Boolean getHandled() {
        return this.handled;
    }

}
