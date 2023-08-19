package com.danieldigiovanni.email.error;

import com.danieldigiovanni.email.code.exception.NotYourCodeException;
import com.danieldigiovanni.email.emailer.exception.ApiCallResponseBodyException;
import com.danieldigiovanni.email.emailer.exception.ApiCallStatusException;
import com.danieldigiovanni.email.emailer.exception.InvalidUrlException;
import com.danieldigiovanni.email.emailer.exception.MailtrapEmailerException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Handles exceptions thrown by controllers.
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    private final String VALIDATION_ERROR = "Validation Error";
    private final String AUTHENTICATION_ERROR = "Authentication Error";
    private final String NOT_FOUND_ERROR = "Not Found Error";
    private final String ALREADY_EXISTS_ERROR = "Already Exists Error";
    private final String NOT_YOUR_CODE_ERROR = "Not Your Code";
    private final String EMAILER_ERROR = "Emailer Error";

    private final Logger log =
        LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorResponseBody handleValidationException(ValidationException exception) {
        return ErrorResponseBody.handledErrorResponse(
            this.VALIDATION_ERROR,
            exception.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseBody handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String errorDetail = exception.getFieldErrors().stream()
            .map(fieldError -> String.format(
                "%s %s",
                fieldError.getField(),
                fieldError.getDefaultMessage() != null
                    ? fieldError.getDefaultMessage()
                    : "unknown validation error"
            ))
            .collect(Collectors.joining(", "));

        return ErrorResponseBody.handledErrorResponse(
            this.VALIDATION_ERROR,
            errorDetail
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponseBody handleAuthenticationException(AuthenticationException exception) {
        return ErrorResponseBody.handledErrorResponse(
            this.AUTHENTICATION_ERROR,
            exception.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponseBody handleNotFoundException(EntityNotFoundException exception) {
        return ErrorResponseBody.handledErrorResponse(
            this.NOT_FOUND_ERROR,
            exception.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public ErrorResponseBody handleAlreadyExistsException(EntityExistsException exception) {
        return ErrorResponseBody.handledErrorResponse(
            this.ALREADY_EXISTS_ERROR,
            exception.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotYourCodeException.class)
    public ErrorResponseBody handleNotYourCodeException(NotYourCodeException exception) {
        return ErrorResponseBody.handledErrorResponse(
            this.NOT_YOUR_CODE_ERROR,
            "You do not have access to the code you tried to verify"
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ApiCallStatusException.class)
    public ErrorResponseBody handleApiCallStatusException(ApiCallStatusException exception) {
        this.log.error(exception.generateLogMessage());
        return ErrorResponseBody.handledErrorResponse(
            this.EMAILER_ERROR,
            exception.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ApiCallResponseBodyException.class)
    public ErrorResponseBody handleApiCallResponseBodyException(ApiCallResponseBodyException exception) {
        this.log.error(exception.generateLogMessage());
        return ErrorResponseBody.handledErrorResponse(
            this.EMAILER_ERROR,
            exception.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InvalidUrlException.class)
    public ErrorResponseBody handleInvalidUrlException(InvalidUrlException exception) {
        return ErrorResponseBody.handledErrorResponse(
            this.EMAILER_ERROR,
            exception.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MailtrapEmailerException.class)
    public ErrorResponseBody handleMailtrapEmailerException(MailtrapEmailerException exception) {
        this.log.error(exception.getMessage(), exception);
        return ErrorResponseBody.handledErrorResponse(
            this.EMAILER_ERROR,
            exception.getMessage()
        );
    }

}
