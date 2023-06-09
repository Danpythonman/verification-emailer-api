package com.danieldigiovanni.email.utils;

import jakarta.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class AuthUtils {

    /**
     * Throws exception if password constraints are not satisfied.
     * <p>
     * The constraints are:
     * <ul>
     *     <li>6 or more characters</li>
     *     <li>at least one lower case letter</li>
     *     <li>at least one upper case letter</li>
     *     <li>at least one number</li>
     * </ul>
     *
     * @param password The password to be validated.
     * @throws ValidationException Thrown if password is not valid.
     */
    public static void checkPasswordValidity(String password) throws ValidationException {
        final int MINIMUM_PASSWORD_LENGTH = 6;
        List<String> errorMessages = new ArrayList<>();

        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            errorMessages.add(
                "Password length must have minimum "
                    + MINIMUM_PASSWORD_LENGTH
                    + " characters"
            );
        }
        if (!password.matches(".*[0-9].*")) {
            errorMessages.add("Password must have at least 1 number");
        }
        if (!password.matches(".*[a-z].*")) {
            errorMessages.add("Password must have at least 1 lower case letter");
        }
        if (!password.matches(".*[A-Z].*")) {
            errorMessages.add("Password must have at least 1 upper case letter");
        }

        if (!errorMessages.isEmpty()) {
            throw new ValidationException(String.join(", ", errorMessages));
        }
    }

}
