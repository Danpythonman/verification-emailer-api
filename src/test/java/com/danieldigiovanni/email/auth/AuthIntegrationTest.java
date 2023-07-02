package com.danieldigiovanni.email.auth;

import com.danieldigiovanni.email.AddServletPathRequestPostProcessor;
import com.danieldigiovanni.email.TestUtils;
import com.danieldigiovanni.email.auth.dto.LoginRequest;
import com.danieldigiovanni.email.auth.dto.RegisterRequest;
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
    public void testRegister_HappyPath() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 1.1",
            "customer1.1@email.com",
            "Password123",
            "Password123"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isOk());
    }

    @Test
    public void testRegister_AlreadyExists() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 1.2",
            "customer1.2@email.com",
            "Password123",
            "Password123"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isOk());

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isConflict());
    }

    @Test
    public void testRegister_InvalidEmail() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 1.3",
            "customer1.3.com",
            "Password123",
            "Password123"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_InvalidPassword() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 1.4",
            "customer1.4@email.com",
            "pw",
            "pw"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isBadRequest());

        body.setPassword("password");
        body.setConfirmPassword("password");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isBadRequest());

        body.setPassword("PASSWORD");
        body.setConfirmPassword("PASSWORD");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isBadRequest());

        body.setPassword("password123");
        body.setConfirmPassword("password123");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isBadRequest());

        body.setPassword("PASSWORD123");
        body.setConfirmPassword("PASSWORD123");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isBadRequest());

        body.setPassword("123456789");
        body.setConfirmPassword("123456789");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_MissingConfirmPassword() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 1.5",
            "customer1.5@email.com",
            "Password123",
            null
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_PasswordsDoNotMatch() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 1.6",
            "customer1.6@email.com",
            "Password123",
            "321Password"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateRegisterRequestBody(body))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterAndLogin_HappyPath() throws Exception {
        String path = "/register";
        RegisterRequest registerBody = new RegisterRequest(
            "Customer 1.7",
            "customer1.7@email.com",
            "Password123",
            "Password123"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateRegisterRequestBody(registerBody)
                    )
            )
            .andExpect(status().isOk());

        path = "/login";
        LoginRequest loginBody = new LoginRequest(
            "customer1.7@email.com",
            "Password123"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateLoginRequestBody(loginBody))
            )
            .andExpect(status().isOk());
    }

    @Test
    public void testRegisterAndLogin_IncorrectPassword() throws Exception {
        String path = "/register";
        RegisterRequest registerBody = new RegisterRequest(
            "Customer 1.8",
            "customer1.8@email.com",
            "Password123",
            "Password123"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateRegisterRequestBody(registerBody)
                    )
            )
            .andExpect(status().isOk());

        path = "/login";
        LoginRequest loginBody = new LoginRequest(
            "customer1.8@email.com",
            "WrongPassword"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateLoginRequestBody(loginBody))
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_NotFound() throws Exception {
        String path = "/login";
        LoginRequest body = new LoginRequest(
            "notfound@doesntexist.com",
            "Password123"
        );

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateLoginRequestBody(body)
                    )
            )
            .andExpect(status().isNotFound());
    }

}
