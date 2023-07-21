package com.danieldigiovanni.email;

import com.danieldigiovanni.email.auth.dto.AuthResponse;
import com.danieldigiovanni.email.auth.dto.LoginRequest;
import com.danieldigiovanni.email.auth.dto.RegisterRequest;
import com.danieldigiovanni.email.customer.Customer;
import com.danieldigiovanni.email.customer.dto.UpdatePasswordRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Helper methods for tests.
 */
public class TestUtils {

    private TestUtils() { }

    public static <T> String generateBody(T request) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(request);
    }

    public static <T> T parseJson(String jsonString, Class<T> objectType) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, objectType);
    }

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
     * Given am {@link UpdatePasswordRequest} object, generates a JSON string.
     *
     * @param updatePasswordRequest The update password request to convert to
     *                              JSON.
     *
     * @return The JSON string corresponding to the given update password
     * request.
     *
     * @throws JsonProcessingException If JSON processing fails.
     */
    public static String generateUpdatePasswordRequestBody(UpdatePasswordRequest updatePasswordRequest) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(updatePasswordRequest);
    }

    /**
     * Given a {@link Map} object, generates a JSON string.
     *
     * @param map The map to convert to JSON.
     *
     * @return The JSON string corresponding to the given map.
     *
     * @throws JsonProcessingException If JSON processing fails.
     */
    public static <K, V> String generateMapBody(Map<K, V> map) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(map);
    }

    /**
     * Given a JSON string, generates an {@link Customer} object.
     *
     * @param jsonString The JSON string to convert to a customer.
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

}
