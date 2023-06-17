package com.danieldigiovanni.email.auth;

import com.danieldigiovanni.email.AddServletPathRequestPostProcessor;
import com.danieldigiovanni.email.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    private final MockMvc mockMvc;

    @Autowired
    public AuthIntegrationTest(MockMvc mockMvc) {
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
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
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
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isOk());

        this.mockMvc.perform(
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isConflict());
    }

    @Test
    public void testRegister_InvalidEmail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
            "Customer 3",
            "customer3.com",
            "Password123",
            "Password123"
        );

        this.mockMvc.perform(
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
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
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");

        this.mockMvc.perform(
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("PASSWORD");
        registerRequest.setConfirmPassword("PASSWORD");

        this.mockMvc.perform(
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        this.mockMvc.perform(
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("PASSWORD123");
        registerRequest.setConfirmPassword("PASSWORD123");

        this.mockMvc.perform(
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());

        registerRequest.setPassword("123456789");
        registerRequest.setConfirmPassword("123456789");

        this.mockMvc.perform(
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
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
                post("/register")
                    .with(new AddServletPathRequestPostProcessor("/register"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(registerRequest))
            )
            .andExpect(status().isBadRequest());
    }

}
