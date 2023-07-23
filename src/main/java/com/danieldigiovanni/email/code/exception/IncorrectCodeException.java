package com.danieldigiovanni.email.code.exception;

import com.danieldigiovanni.email.code.Code;
import com.danieldigiovanni.email.code.dto.CodeResponse;

/**
 * Exception representing the case where the verification code that the user
 * sent is incorrect (does not match the code that was actually created).
 */
public class IncorrectCodeException extends RuntimeException {

    private final Code code;

    /**
     * Constructs an InvalidCodeException exception with an instance of the
     * code that produced the exception (by the user failing to verify the
     * code).
     * <p>
     * The exception message is created as "Incorrect code provided".
     *
     * @param code The code that the user failed to verify.
     */
    public IncorrectCodeException(Code code) {
        super("Incorrect code provided");
        this.code = code;
    }

    /**
     * Generates an API response corresponding to the code that produced this
     * exception.
     *
     * @return API response corresponding to this exception.
     */
    public CodeResponse generateResponse() {
        return new CodeResponse(this.code);
    }

}
