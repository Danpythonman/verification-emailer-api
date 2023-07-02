package com.danieldigiovanni.email.customer;

import com.danieldigiovanni.email.AddServletPathRequestPostProcessor;
import com.danieldigiovanni.email.TestUtils;
import com.danieldigiovanni.email.auth.AuthResponse;
import com.danieldigiovanni.email.auth.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 1",
            "customer1@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.readJsonIntoAuthResponse(
            result.getResponse().getContentAsString()
        );

        path = "/customer";

        result = this.mockMvc.perform(
                get(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
            )
            .andExpect(status().isOk())
            .andReturn();

        Customer customer = TestUtils.readJsonIntoCustomer(
            result.getResponse().getContentAsString()
        );

        assertNull(customer.getId());
        assertNull(customer.getPassword());
    }

    @Test
    public void testRegisterCustomerAndUpdateName_HappyPath() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 2",
            "customer2@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.readJsonIntoAuthResponse(
            result.getResponse().getContentAsString()
        );

        path = "/customer";
        String newName = "New Name";

        result = this.mockMvc.perform(
                patch(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateMapBody(Map.of("name", newName))
                    )
            )
            .andExpect(status().isOk())
            .andReturn();

        Customer customer = TestUtils.readJsonIntoCustomer(
            result.getResponse().getContentAsString()
        );

        assertEquals(newName, customer.getName());
    }

    @Test
    public void testRegisterCustomerAndUpdateName_InvalidBody() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 3",
            "customer3@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.readJsonIntoAuthResponse(
            result.getResponse().getContentAsString()
        );

        path = "/customer";

        this.mockMvc.perform(
                patch(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateMapBody(
                            Map.of("email", "new@email.com")
                        )
                    )
            )
            .andExpect(status().isBadRequest());
    }

}
