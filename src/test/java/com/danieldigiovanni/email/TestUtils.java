package com.danieldigiovanni.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper methods for tests.
 */
public class TestUtils {

    private TestUtils() { }

    /**
     * Generates a JSON string from an object to be used as a request body.
     *
     * @param request The object to be converted to JSON.
     *
     * @return The JSON string representing the object.
     *
     * @param <T> The type of the object.
     *
     * @throws JsonProcessingException If JSON processing fails.
     */
    public static <T> String generateJson(T request) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(request);
    }

    /**
     * Generates an object from JSON received from an API response.
     *
     * @param jsonString The string to be parsed into an object.
     * @param objectType The class of the object.
     *
     * @return An object corresponding to the given JSON.
     *
     * @param <T> The type of the object.
     *
     * @throws JsonProcessingException If JSON processing fails.
     */
    public static <T> T parseJson(String jsonString, Class<T> objectType) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, objectType);
    }

}
