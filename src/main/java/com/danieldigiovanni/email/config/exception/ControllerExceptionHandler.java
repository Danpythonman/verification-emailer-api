package com.danieldigiovanni.email.config.exception;

import com.danieldigiovanni.email.code.dto.CodeResponse;
import com.danieldigiovanni.email.code.exception.IncorrectCodeException;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles exceptions thrown by controllers.
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    private final Logger log =
        LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException exception) {
        return exception.getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fieldError -> fieldError.getDefaultMessage() == null
                    ? "Unknown error"
                    : fieldError.getDefaultMessage()
            ));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthenticationException(AuthenticationException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFoundException(EntityNotFoundException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public String handleAlreadyExistsException(EntityExistsException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectCodeException.class)
    public CodeResponse handleIncorrectCodeException(IncorrectCodeException exception) {
        return exception.generateResponse();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotYourCodeException.class)
    public String handleNotYourCodeException(NotYourCodeException exception) {
        return "You do not have access to the code you tried to verify";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ApiCallStatusException.class)
    public String handleApiCallStatusException(ApiCallStatusException exception) {
        this.log.error(exception.generateLogMessage());
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ApiCallResponseBodyException.class)
    public String handleApiCallResponseBodyException(ApiCallResponseBodyException exception) {
        this.log.error(exception.generateLogMessage());
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InvalidUrlException.class)
    public String handleInvalidUrlException(InvalidUrlException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MailtrapEmailerException.class)
    public String handleMailtrapEmailerException(MailtrapEmailerException exception) {
        this.log.error(exception.getMessage(), exception);
        return exception.getMessage();
    }

}
