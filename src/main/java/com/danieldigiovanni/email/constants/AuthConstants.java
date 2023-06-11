package com.danieldigiovanni.email.constants;

/**
 * Constants for authentication and authorization.
 */
public final class AuthConstants {

    private AuthConstants() { }

    /**
     * The whitelisted routes that do not require any authorization.
     */
    public static final String[] WHITELISTED_ROUTES = {
        "/register",
        "/login"
    };

}
