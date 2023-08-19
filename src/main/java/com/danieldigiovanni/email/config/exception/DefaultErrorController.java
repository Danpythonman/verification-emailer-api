package com.danieldigiovanni.email.config.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * This error controller replaces {@link BasicErrorController}. It handles all
 * unhandled exceptions before a response is sent back to the user.
 * <p>
 * The exceptions handled in {@link ControllerExceptionHandler} are not handled
 * here. Any <i>other</i> exception eventually makes it to here, where we can
 * control what information the user sees.
 * <p>
 * The purpose of this class is to log unexpected errors and to enforce a
 * consistent format for the error response body.
 */
@RestController
public class DefaultErrorController implements ErrorController {

    private final Logger log =
        LoggerFactory.getLogger(DefaultErrorController.class);

    @RequestMapping("${server.error.path:${error.path:/error}}")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseBody error(HttpServletRequest request) {
        // JSON deserialization errors (which seem to be
        // HttpMessageNotReadableException) end up as null here.
        Exception exception = (Exception) request.getAttribute(
            RequestDispatcher.ERROR_EXCEPTION
        );

        if (exception != null) {
            this.log.error(
                "Unhandled exception made it to the DefaultErrorController. " +
                    "Sending error back to user.\n" +
                    "Exception: {}, message: {}, stacktrace: {}",
                exception.getClass().getName(),
                exception.getMessage(),
                exception.getStackTrace()
            );

            return ErrorResponseBody.unHandledErrorResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
            );
        } else {
            return ErrorResponseBody.unHandledErrorResponse(
                "Unknown Error",
                "We know there is an error, but we don't know what it is"
            );
        }
    }

}
