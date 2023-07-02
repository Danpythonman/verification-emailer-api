package com.danieldigiovanni.email.customer;

import com.danieldigiovanni.email.AddServletPathRequestPostProcessor;
import com.danieldigiovanni.email.TestUtils;
import com.danieldigiovanni.email.auth.AuthResponse;
import com.danieldigiovanni.email.auth.RegisterRequest;
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

    @Test
    public void testRegisterCustomerAndUpdatePassword_HappyPath() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 4",
            "customer4@email.com",
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

        path = "/customer/password";

        this.mockMvc.perform(
                put(path)
                    .header(
                        "Authorization", "Bearer " + authResponse.getToken()
                    )
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateMapBody(
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
                        TestUtils.generateMapBody(
                            Map.of(
                                "email", "customer4@email.com",
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
                        TestUtils.generateMapBody(
                            Map.of(
                                "email", "customer4@email.com",
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
            "Customer 5",
            "customer5@email.com",
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

        path = "/customer/password";

        UpdatePasswordRequest updateBody = new UpdatePasswordRequest();
        updateBody.setOldPassword("incorrectPassword");
        updateBody.setNewPassword("312Password");
        updateBody.setConfirmPassword("312Password");

        this.mockMvc.perform(
                put(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateUpdatePasswordRequestBody(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterCustomerAndUpdatePassword_InvalidPassword() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 6",
            "customer6@email.com",
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

        path = "/customer/password";

        UpdatePasswordRequest updateBody = new UpdatePasswordRequest();
        updateBody.setOldPassword("Password123");
        updateBody.setNewPassword("pw");
        updateBody.setConfirmPassword("pw");

        this.mockMvc.perform(
                put(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateUpdatePasswordRequestBody(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("password");
        updateBody.setConfirmPassword("password");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateUpdatePasswordRequestBody(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("PASSWORD");
        updateBody.setConfirmPassword("PASSWORD");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateUpdatePasswordRequestBody(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("password123");
        updateBody.setConfirmPassword("password123");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateUpdatePasswordRequestBody(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("PASSWORD123");
        updateBody.setConfirmPassword("PASSWORD123");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateUpdatePasswordRequestBody(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());

        updateBody.setNewPassword("123456789");
        updateBody.setConfirmPassword("123456789");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateUpdatePasswordRequestBody(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterCustomerAndUpdatePassword_ConfirmPasswordDoesNotMatch() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 7",
            "customer7@email.com",
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

        path = "/customer/password";

        UpdatePasswordRequest updateBody = new UpdatePasswordRequest();
        updateBody.setOldPassword("Password123");
        updateBody.setNewPassword("312Password");
        updateBody.setConfirmPassword("Pass321word");

        this.mockMvc.perform(
                put(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateUpdatePasswordRequestBody(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterCustomerAndUpdatePassword_NewPasswordSameAsOldPassword() throws Exception {
        String path = "/register";
        RegisterRequest body = new RegisterRequest(
            "Customer 8",
            "customer8@email.com",
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

        path = "/customer/password";

        UpdatePasswordRequest updateBody = new UpdatePasswordRequest();
        updateBody.setOldPassword("Password123");
        updateBody.setNewPassword("Password123");
        updateBody.setConfirmPassword("Password123");

        this.mockMvc.perform(
                put(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateUpdatePasswordRequestBody(
                        updateBody
                    ))
            )
            .andExpect(status().isBadRequest());
    }

}
