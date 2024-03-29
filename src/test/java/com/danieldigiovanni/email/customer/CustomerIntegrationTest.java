package com.danieldigiovanni.email.customer;

import com.danieldigiovanni.email.AddServletPathRequestPostProcessor;
import com.danieldigiovanni.email.TestUtils;
import com.danieldigiovanni.email.auth.dto.AuthResponse;
import com.danieldigiovanni.email.auth.dto.RegisterRequest;
import com.danieldigiovanni.email.customer.dto.UpdatePasswordRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
            "Customer 2.1",
            "customer2.1@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            AuthResponse.class
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

        Customer customer = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Customer.class
        );

        assertNull(customer.getId());
        assertNull(customer.getPassword());
    }

    @Test
    public void testRegisterCustomerAndUpdateName_HappyPath() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 2.2",
            "customer2.2@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            AuthResponse.class
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
                        TestUtils.generateJson(Map.of("name", newName))
                    )
            )
            .andExpect(status().isOk())
            .andReturn();

        Customer customer = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Customer.class
        );

        assertEquals(newName, customer.getName());
    }

    @Test
    public void testRegisterCustomerAndUpdateName_InvalidBody() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 2.3",
            "customer2.3@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            AuthResponse.class
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
                        TestUtils.generateJson(
                            Map.of("email", "new@email.com")
                        )
                    )
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterCustomerAndUpdatePassword_HappyPath() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 2.4",
            "customer2.4@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            AuthResponse.class
        );

        path = "/customer/password";

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateJson(
                            Map.of(
                                "oldPassword", "Password123",
                                "newPassword", "312Password",
                                "confirmPassword", "312Password"
                            )
                        )
                    )
            )
            .andExpect(status().isOk());

        path = "/login";

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateJson(
                            Map.of(
                                "email", "customer2.4@email.com",
                                "password", "Password123"
                            )
                        )
                    )
            )
            .andExpect(status().isUnauthorized());

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateJson(
                            Map.of(
                                "email", "customer2.4@email.com",
                                "password", "312Password"
                            )
                        )
                    )
            )
            .andExpect(status().isOk());
    }

    @Test
    public void testRegisterCustomerAndUpdatePassword_OldPasswordIncorrect() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 2.5",
            "customer2.5@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            AuthResponse.class
        );

        path = "/customer/password";

        UpdatePasswordRequest updateBody = new UpdatePasswordRequest();
        updateBody.setOldPassword("incorrectPassword");
        updateBody.setNewPassword("312Password");
        updateBody.setConfirmPassword("312Password");

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterCustomerAndUpdatePassword_InvalidPassword() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 2.6",
            "customer2.6@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            AuthResponse.class
        );

        path = "/customer/password";

        UpdatePasswordRequest updateBody = new UpdatePasswordRequest();
        updateBody.setOldPassword("Password123");
        updateBody.setNewPassword("pw");
        updateBody.setConfirmPassword("pw");

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("password");
        updateBody.setConfirmPassword("password");

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("PASSWORD");
        updateBody.setConfirmPassword("PASSWORD");

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("password123");
        updateBody.setConfirmPassword("password123");

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("PASSWORD123");
        updateBody.setConfirmPassword("PASSWORD123");

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("123456789");
        updateBody.setConfirmPassword("123456789");

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterCustomerAndUpdatePassword_ConfirmPasswordDoesNotMatch() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 2.7",
            "customer2.7@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            AuthResponse.class
        );

        path = "/customer/password";

        UpdatePasswordRequest updateBody = new UpdatePasswordRequest();
        updateBody.setOldPassword("Password123");
        updateBody.setNewPassword("312Password");
        updateBody.setConfirmPassword("Pass321word");

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterCustomerAndUpdatePassword_NewPasswordSameAsOldPassword() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 2.8",
            "customer2.8@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            AuthResponse.class
        );

        path = "/customer/password";

        UpdatePasswordRequest updateBody = new UpdatePasswordRequest();
        updateBody.setOldPassword("Password123");
        updateBody.setNewPassword("Password123");
        updateBody.setConfirmPassword("Password123");

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteUser() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 2.9",
            "customer2.9@email.com",
            "Password123",
            "Password123"
        );

        MvcResult result = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(body))
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            result.getResponse().getContentAsString(), AuthResponse.class
        );

        path = "/login";

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(Map.of(
                        "email", body.getEmail(),
                        "password", body.getPassword()
                    )))
            )
            .andExpect(status().isOk())
            .andReturn();

        path = "/customer";

        this.mockMvc.perform(
                delete(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent());

        path = "/login";

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(Map.of(
                        "email", body.getEmail(),
                        "password", body.getPassword()
                    )))
            )
            .andExpect(status().isNotFound())
            .andReturn();
    }

}
