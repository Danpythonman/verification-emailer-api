package com.danieldigiovanni.email.code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Component
public class CodeUtils {

    private final PasswordEncoder codeHasher;

    @Autowired
    public CodeUtils(PasswordEncoder codeHasher) {
        this.codeHasher = codeHasher;
    }

    /**
     * Generates a random string of numbers of the specified length.
     *
     * @param length The length of the random string.
     *
     * @return A random string of {@code length} numbers.
     */
    public String generateRandomCode(int length) {
        return new SecureRandom().ints(length, 0, 10)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining());
    }

    /**
     * Generates a hash of a code.
     *
     * @param code The code (in plain-text) to be hashed.
     *
     * @return The hash of the provided code.
     */
    public String generateHash(String code) {
        return this.codeHasher.encode(code);
    }

    /**
     * Checks if a code matches with a hash.
     *
     * @param rawCode    The code to compare with the hash.
     * @param hashedCode The hash to compare with the code.
     *
     * @return True if the code matches with the hash.
     */
    public boolean matches(String rawCode, String hashedCode) {
        return this.codeHasher.matches(rawCode, hashedCode);
    }

}
