package com.danieldigiovanni.email.code;

import com.danieldigiovanni.email.code.dto.CodeResponse;
import com.danieldigiovanni.email.code.dto.SendCodeRequest;
import com.danieldigiovanni.email.code.dto.VerifyCodeRequest;
import com.danieldigiovanni.email.code.exception.IncorrectCodeException;
import com.danieldigiovanni.email.emailer.exception.ApiCallResponseBodyException;
import com.danieldigiovanni.email.emailer.exception.ApiCallStatusException;
import com.danieldigiovanni.email.emailer.exception.InvalidUrlException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CodeController {

    private final CodeService codeService;
    private final Logger log = LoggerFactory.getLogger(CodeController.class);

    @Autowired
    public CodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    @PostMapping("/code/send")
    public CodeResponse sendCode(@RequestBody @Valid SendCodeRequest sendCodeRequest) {
        return this.codeService.sendCode(sendCodeRequest);
    }

    @PostMapping("/code/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CodeResponse verifyCode(@RequestBody @Valid VerifyCodeRequest verifyCodeRequest) {
        return this.codeService.verifyCode(verifyCodeRequest);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectCodeException.class)
    public CodeResponse handleIncorrectCodeException(IncorrectCodeException exception) {
        return exception.generateResponse();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ApiCallStatusException.class)
    public String handleApiCallStatusException(ApiCallStatusException exception) {
        log.error(exception.generateLogMessage());
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ApiCallResponseBodyException.class)
    public String handleApiCallResponseBodyException(ApiCallResponseBodyException exception) {
        log.error(exception.generateLogMessage());
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InvalidUrlException.class)
    public String handleInvalidUrlException(InvalidUrlException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public String handleAlreadyExistsException(EntityExistsException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFoundException(EntityNotFoundException exception) {
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

}
