package com.danieldigiovanni.email.customer;

import com.danieldigiovanni.email.ContextPathRequestPostProcessor;
import com.danieldigiovanni.email.auth.RegisterRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
    public void testRegisterCustomer_HappyPath() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
            "Customer 1",
            "customer1@email.com",
            "Password123",
            "Password123"
        );

        this.mockMvc.perform(
                post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isOk());
    }

    @Test
    public void testRegister_AlreadyExists() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
            "Customer 2",
            "customer2@email.com",
            "Password123",
            "Password123"
        );

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isOk());

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isConflict());
    }

    @Test
    public void testRegister_InvalidEmail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
            "Customer 3",
            "customer3@email.com",
            "Password123",
            "Password123"
        );

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_InvalidPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
            "Customer 4",
            "customer4@email.com",
            "pw",
            "pw"
        );

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("PASSWORD");
        registerRequest.setConfirmPassword("PASSWORD");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("PASSWORD123");
        registerRequest.setConfirmPassword("PASSWORD123");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("123456789");
        registerRequest.setConfirmPassword("123456789");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_PasswordsDoNotMatch() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
            "Customer 5",
            "customer5@email.com",
            "Password123",
            "321Password"
        );

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());
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
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isOk())
            .andReturn();

        Customer newCustomer = this.readJsonIntoCustomer(
            result.getResponse().getContentAsString()
        );

        this.mockMvc.perform(
                get("/customer/" + newCustomer.getId())
            )
            .andExpect(status().isOk());
    }

    @Test
    public void testCreateAndGetCustomer_CustomerDoesNotExist() throws Exception {
        this.mockMvc.perform(
                get("/customer/9999")
            )
            .andExpect(status().isNotFound());
    }

    /**
     * Given a register request object, generate a JSON string.
     *
     * @param registerRequest The register request object to convert to JSON.
     * @return The JSON string corresponding to the given register request object.
     * @throws JsonProcessingException When JSON processing fails.
     */
    private String generateRegisterRequestBody(RegisterRequest registerRequest) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(registerRequest);
    }

    /**
     * Given a customer object, generate a JSON string.
     *
     * @param customer The customer object to convert to JSON.
     * @return The JSON string corresponding to the given customer object.
     * @throws JsonProcessingException When JSON processing fails.
     */
    private String generateCreateCustomerRequestBody(Customer customer) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(customer);
    }

    /**
     * Given a JSON string, generates a customer object.
     *
     * @param jsonString The JSON string to convert to a customer object.
     * @return The customer object corresponding to the JSON string.
     * @throws JsonProcessingException When JSON processing fails.
     */
    private Customer readJsonIntoCustomer(String jsonString) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, Customer.class);
    }

}
