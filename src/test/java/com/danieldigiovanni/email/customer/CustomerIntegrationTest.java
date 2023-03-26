package com.danieldigiovanni.email.customer;

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
    public void testCreateCustomer_HappyPath() throws Exception {
        Customer customer = new Customer();
        customer.setName("Customer 1");
        customer.setEmail("customer1@email.com");
        customer.setPassword("Password123");
        customer.setConfirmPassword("Password123");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isOk());
    }

    @Test
    public void testCreateCustomer_AlreadyExists() throws Exception {
        Customer customer = new Customer();
        customer.setName("Customer 2");
        customer.setEmail("customer2@email.com");
        customer.setPassword("Password123");
        customer.setConfirmPassword("Password123");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isOk());

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isConflict());
    }

    @Test
    public void testCreateCustomer_InvalidEmail() throws Exception {
        Customer customer = new Customer();
        customer.setName("Customer 3");
        customer.setEmail("invalid-email.com");
        customer.setPassword("Password123");
        customer.setConfirmPassword("Password123");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCustomer_InvalidPassword() throws Exception {
        Customer customer = new Customer();
        customer.setName("Customer 4");
        customer.setEmail("customer4@email.com");
        customer.setPassword("pw");
        customer.setConfirmPassword("pw");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isBadRequest());

        customer.setPassword("password");
        customer.setConfirmPassword("password");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isBadRequest());

        customer.setPassword("PASSWORD");
        customer.setConfirmPassword("PASSWORD");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isBadRequest());

        customer.setPassword("password123");
        customer.setConfirmPassword("password123");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isBadRequest());

        customer.setPassword("PASSWORD123");
        customer.setConfirmPassword("PASSWORD123");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isBadRequest());

        customer.setPassword("123456789");
        customer.setConfirmPassword("123456789");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCustomer_PasswordsDoNotMatch() throws Exception {
        Customer customer = new Customer();
        customer.setName("Customer 5");
        customer.setEmail("customer5@email.com");
        customer.setPassword("Password123");
        customer.setConfirmPassword("Password321");

        this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateAndGetCustomer_HappyPath() throws Exception {
        Customer customer = new Customer();
        customer.setName("Customer 6");
        customer.setEmail("customer6@email.com");
        customer.setPassword("Password123");
        customer.setConfirmPassword("Password123");

        MvcResult result = this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.generateCreateCustomerRequestBody(customer))
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
