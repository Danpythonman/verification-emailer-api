package com.danieldigiovanni.email;

import com.danieldigiovanni.email.auth.AuthResponse;
import com.danieldigiovanni.email.auth.LoginRequest;
import com.danieldigiovanni.email.auth.RegisterRequest;
import com.danieldigiovanni.email.customer.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Helper methods for tests.
 */
public class TestUtils {

    private TestUtils() { }

    /**
     * Given a {@link RegisterRequest} object, generates a JSON string.
     *
     * @param registerRequest The register request to convert to JSON.
     *
     * @return The JSON string corresponding to the given register request.
     *
     * @throws JsonProcessingException If JSON processing fails.
     */
    public static String generateRegisterRequestBody(RegisterRequest registerRequest) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(registerRequest);
    }

    /**
     * Given a {@link LoginRequest} object, generates a JSON string.
     *
     * @param loginRequest The login request to convert to JSON.
     *
     * @return The JSON string corresponding to the given login request.
     *
     * @throws JsonProcessingException If JSON processing fails.
     */
    public static String generateLoginRequestBody(LoginRequest loginRequest) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(loginRequest);
    }

    /**
     * Given a JSON string, generates an {@link Customer} object.
     *
     * @param jsonString The JSON string to convert to an customer.
     *
     * @return The customer corresponding to the JSON string.
     *
     * @throws JsonProcessingException If JSON processing fails.
     */
    public static Customer readJsonIntoCustomer(String jsonString) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, Customer.class);
    }

    /**
     * Given a JSON string, generates an {@link AuthResponse} object.
     *
     * @param jsonString The JSON string to convert to an auth response.
     *
     * @return The auth response corresponding to the JSON string.
     *
     * @throws JsonProcessingException If JSON processing fails.
     */
    public static AuthResponse readJsonIntoAuthResponse(String jsonString) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, AuthResponse.class);
    }

    /**
     * Parses a JWT without validating the signature.
     *
     * @param jwt The JWT to be parsed.
     *
     * @return The claims from the JWT.
     *
     * @throws IllegalArgumentException If the JWT is null, empty, or only
     *                                  whitespace.
     * @throws MalformedJwtException    If the JWT does not have a valid
     *                                  format.
     * @throws UnsupportedJwtException  If the JWT claims do not have a valid
     *                                  format.
     * @throws ExpiredJwtException      If the JWT is expired.
     */
    public static Claims extractClaimsFromToken(String jwt) throws IllegalArgumentException, MalformedJwtException, UnsupportedJwtException, ExpiredJwtException {
        String[] jwtParts = jwt.split("\\.");
        String unsignedJwt = jwtParts[0] + "." + jwtParts[1] + ".";

        JwtParser jwtParser = Jwts.parserBuilder().build();

        return jwtParser.parseClaimsJwt(unsignedJwt).getBody();
    }

}
