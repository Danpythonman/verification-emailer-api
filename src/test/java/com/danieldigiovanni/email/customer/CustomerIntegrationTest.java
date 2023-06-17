package com.danieldigiovanni.email.customer;

import com.danieldigiovanni.email.AddServletPathRequestPostProcessor;
import com.danieldigiovanni.email.auth.AuthResponse;
import com.danieldigiovanni.email.auth.RegisterRequest;
import com.danieldigiovanni.email.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerIntegrationTest {

    private final MockMvc mockMvc;

    @Autowired
    public CustomerIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void testRegisterAndGetCustomer_HappyPath() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
            "Customer 6",
            "customer6@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = this.readJsonIntoAuthResponse(
            result.getResponse().getContentAsString()
        );

        String id = JwtUtils.extractClaimsFromToken(authResponse.getToken()).getSubject();

        this.mockMvc.perform(
                get("/customer/" + id)
                    .header("Authorization", "Bearer " + authResponse.getToken())
            )
            .andExpect(status().isOk());
    }

    /**
     * Given a register request object, generate a JSON string.
     *
     * @param registerRequest The register request object to convert to JSON.
     *
     * @return The JSON string corresponding to the given register request object.
     *
     * @throws JsonProcessingException When JSON processing fails.
     */
    private String generateRegisterRequestBody(RegisterRequest registerRequest) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(registerRequest);
    }

    /**
     * Given a customer object, generate a JSON string.
     *
     * @param customer The customer object to convert to JSON.
     *
     * @return The JSON string corresponding to the given customer object.
     *
     * @throws JsonProcessingException When JSON processing fails.
     */
    private String generateCreateCustomerRequestBody(Customer customer) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(customer);
    }

    /**
     * Given a JSON string, generates a customer object.
     *
     * @param jsonString The JSON string to convert to a customer object.
     *
     * @return The customer object corresponding to the JSON string.
     *
     * @throws JsonProcessingException When JSON processing fails.
     */
    private Customer readJsonIntoCustomer(String jsonString) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, Customer.class);
    }

    /**
     * Given a JSON string, generates a customer object.
     *
     * @param jsonString The JSON string to convert to a customer object.
     *
     * @return The customer object corresponding to the JSON string.
     *
     * @throws JsonProcessingException When JSON processing fails.
     */
    private AuthResponse readJsonIntoAuthResponse(String jsonString) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, AuthResponse.class);
    }

}
