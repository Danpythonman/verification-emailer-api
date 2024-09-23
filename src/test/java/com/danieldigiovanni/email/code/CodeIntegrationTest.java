package com.danieldigiovanni.email.code;

import com.danieldigiovanni.email.AddServletPathRequestPostProcessor;
import com.danieldigiovanni.email.TestUtils;
import com.danieldigiovanni.email.auth.dto.AuthResponse;
import com.danieldigiovanni.email.auth.dto.RegisterRequest;
import com.danieldigiovanni.email.code.dto.SendCodeRequest;
import com.danieldigiovanni.email.code.dto.CodeResponse;
import com.danieldigiovanni.email.code.dto.SendCustomCodeRequest;
import com.danieldigiovanni.email.code.dto.VerifyCodeRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CodeIntegrationTest {

    @SpyBean
    private CodeUtils codeUtils;

    private final MockMvc mockMvc;

    private String token;

    @Autowired
    public CodeIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public void registerCustomerAndGetToken() throws Exception {
        String path = "/register";
        RegisterRequest registerBody = new RegisterRequest(
            "Customer 3.1",
            "customer3.1@email.com",
            "Password123",
            "Password123"
        );

        MvcResult response = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateJson(registerBody)
                    )
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            response.getResponse().getContentAsString(),
            AuthResponse.class
        );

        this.token = authResponse.getToken();
    }

    @Test
    public void testGenerateAndVerifyCode_HappyPath_OneAttempt() throws Exception {
        doReturn("123").when(codeUtils).generateRandomCode(anyInt());

        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test1@email.com");
        sendCodeRequest.setLength(3);
        sendCodeRequest.setMaximumAttempts(5);
        sendCodeRequest.setMaximumDurationInMinutes(10);

        String path = "/code/send";

        MvcResult response = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCodeRequest))
            )
            .andExpect(status().isOk())
            .andReturn();

        CodeResponse codeResponse = TestUtils.parseJson(
            response.getResponse().getContentAsString(),
            CodeResponse.class
        );

        assertEquals(5, codeResponse.getMaximumAttempts());
        assertEquals(5, codeResponse.getRemainingAttempts());
        assertEquals(
            10,
            TimeUnit.MILLISECONDS.toMinutes(
                codeResponse.getExpiresAt().getTime()
                    - codeResponse.getCreatedAt().getTime()
            )
        );

        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest();
        verifyCodeRequest.setEmail("test1@email.com");
        verifyCodeRequest.setCode("123");

        path = "/code/verify";
        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(verifyCodeRequest))
            )
            .andExpect(status().isNoContent());
    }

    @Test
    public void testGenerateAndVerifyCode_HappyPath_ThreeAttempt() throws Exception {
        doReturn("123").when(codeUtils).generateRandomCode(anyInt());

        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test2@email.com");
        sendCodeRequest.setLength(3);
        sendCodeRequest.setMaximumAttempts(5);
        sendCodeRequest.setMaximumDurationInMinutes(10);

        String path = "/code/send";

        MvcResult response = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCodeRequest))
            )
            .andExpect(status().isOk())
            .andReturn();

        CodeResponse codeResponse = TestUtils.parseJson(
            response.getResponse().getContentAsString(),
            CodeResponse.class
        );

        assertEquals(5, codeResponse.getMaximumAttempts());
        assertEquals(5, codeResponse.getRemainingAttempts());
        assertEquals(
            10,
            TimeUnit.MILLISECONDS.toMinutes(
                codeResponse.getExpiresAt().getTime()
                    - codeResponse.getCreatedAt().getTime()
            )
        );

        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest();
        verifyCodeRequest.setEmail("test2@email.com");
        verifyCodeRequest.setCode("54321");

        path = "/code/verify";
        for (int i = 0 ; i < 2; i++) {
            this.mockMvc.perform(
                    post(path)
                        .with(new AddServletPathRequestPostProcessor(path))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.generateJson(verifyCodeRequest))
                )
                .andExpect(status().isBadRequest());
        }

        verifyCodeRequest.setCode("123");
        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(verifyCodeRequest))
            )
            .andExpect(status().isNoContent());
    }

    @Test
    public void testGenerateAndVerifyCode_TooManyAttempts() throws Exception {
        doReturn("123").when(codeUtils).generateRandomCode(anyInt());

        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test3@email.com");
        sendCodeRequest.setLength(3);
        sendCodeRequest.setMaximumAttempts(5);
        sendCodeRequest.setMaximumDurationInMinutes(10);

        String path = "/code/send";

        MvcResult response = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCodeRequest))
            )
            .andExpect(status().isOk())
            .andReturn();

        CodeResponse codeResponse = TestUtils.parseJson(
            response.getResponse().getContentAsString(),
            CodeResponse.class
        );

        assertEquals(5, codeResponse.getMaximumAttempts());
        assertEquals(5, codeResponse.getRemainingAttempts());
        assertEquals(
            10,
            TimeUnit.MILLISECONDS.toMinutes(
                codeResponse.getExpiresAt().getTime()
                    - codeResponse.getCreatedAt().getTime()
            )
        );

        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest();
        verifyCodeRequest.setEmail("test3@email.com");
        verifyCodeRequest.setCode("54321");

        path = "/code/verify";
        for (int i = 0 ; i < 5; i++) {
            this.mockMvc.perform(
                    post(path)
                        .with(new AddServletPathRequestPostProcessor(path))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.generateJson(verifyCodeRequest))
                )
                .andExpect(status().isBadRequest());
        }

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(verifyCodeRequest))
            )
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGenerateCode_CodeAlreadyExists() throws Exception {
        doReturn("123").when(codeUtils).generateRandomCode(anyInt());

        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test4@email.com");
        sendCodeRequest.setLength(3);
        sendCodeRequest.setMaximumAttempts(5);
        sendCodeRequest.setMaximumDurationInMinutes(10);

        String path = "/code/send";

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCodeRequest))
            )
            .andExpect(status().isOk());

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCodeRequest))
            )
            .andExpect(status().isConflict())
            .andReturn();
    }

    @Test
    public void testGenerateAndVerifyCode_NotYourCode() throws Exception {
        doReturn("123").when(codeUtils).generateRandomCode(anyInt());

        SendCodeRequest sendCodeRequest = new SendCodeRequest();
        sendCodeRequest.setEmail("test5@email.com");
        sendCodeRequest.setLength(3);
        sendCodeRequest.setMaximumAttempts(5);
        sendCodeRequest.setMaximumDurationInMinutes(10);

        String path = "/code/send";

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCodeRequest))
            )
            .andExpect(status().isOk());

        path = "/register";
        RegisterRequest registerBody = new RegisterRequest(
            "Customer 3.2",
            "customer3.2@email.com",
            "Password123",
            "Password123"
        );

        MvcResult response = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        TestUtils.generateJson(registerBody)
                    )
            )
            .andExpect(status().isOk())
            .andReturn();

        AuthResponse authResponse = TestUtils.parseJson(
            response.getResponse().getContentAsString(),
            AuthResponse.class
        );

        String otherUserToken = authResponse.getToken();

        path = "/code/verify";
        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest();
        verifyCodeRequest.setEmail("test5@email.com");
        verifyCodeRequest.setCode("123");

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + otherUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(verifyCodeRequest))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    public void testGenerateAndVerifyCustomCode_HappyPath_OneAttempt() throws Exception {
        SendCustomCodeRequest sendCustomCodeRequest = new SendCustomCodeRequest();
        sendCustomCodeRequest.setEmail("test6@email.com");
        sendCustomCodeRequest.setCode("123");
        sendCustomCodeRequest.setMaximumAttempts(5);
        sendCustomCodeRequest.setMaximumDurationInMinutes(10);

        String path = "/custom/code/send";

        MvcResult response = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCustomCodeRequest))
            )
            .andExpect(status().isOk())
            .andReturn();

        CodeResponse codeResponse = TestUtils.parseJson(
            response.getResponse().getContentAsString(),
            CodeResponse.class
        );

        assertEquals(5, codeResponse.getMaximumAttempts());
        assertEquals(5, codeResponse.getRemainingAttempts());
        assertEquals(
            10,
            TimeUnit.MILLISECONDS.toMinutes(
                codeResponse.getExpiresAt().getTime()
                    - codeResponse.getCreatedAt().getTime()
            )
        );

        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest();
        verifyCodeRequest.setEmail("test6@email.com");
        verifyCodeRequest.setCode("123");

        path = "/code/verify";
        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(verifyCodeRequest))
            )
            .andExpect(status().isNoContent());
    }


    @Test
    public void testGenerateCustomCode_CodeAlreadyExists() throws Exception {
        SendCustomCodeRequest sendCustomCodeRequest = new SendCustomCodeRequest();
        sendCustomCodeRequest.setEmail("test7@email.com");
        sendCustomCodeRequest.setCode("123");
        sendCustomCodeRequest.setMaximumAttempts(5);
        sendCustomCodeRequest.setMaximumDurationInMinutes(10);

        String path = "/custom/code/send";

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCustomCodeRequest))
            )
            .andExpect(status().isOk());

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCustomCodeRequest))
            )
            .andExpect(status().isConflict())
            .andReturn();
    }

    @Test
    public void testGenerateAndVerifyCustomCode_TooManyAttempts() throws Exception {
        SendCustomCodeRequest sendCustomCodeRequest = new SendCustomCodeRequest();
        sendCustomCodeRequest.setEmail("test8@email.com");
        sendCustomCodeRequest.setCode("123");
        sendCustomCodeRequest.setMaximumAttempts(5);
        sendCustomCodeRequest.setMaximumDurationInMinutes(10);

        String path = "/custom/code/send";

        MvcResult response = this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCustomCodeRequest))
            )
            .andExpect(status().isOk())
            .andReturn();

        CodeResponse codeResponse = TestUtils.parseJson(
            response.getResponse().getContentAsString(),
            CodeResponse.class
        );

        assertEquals(5, codeResponse.getMaximumAttempts());
        assertEquals(5, codeResponse.getRemainingAttempts());
        assertEquals(
            10,
            TimeUnit.MILLISECONDS.toMinutes(
                codeResponse.getExpiresAt().getTime()
                    - codeResponse.getCreatedAt().getTime()
            )
        );

        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest();
        verifyCodeRequest.setEmail("test8@email.com");
        verifyCodeRequest.setCode("54321");

        path = "/code/verify";
        for (int i = 0 ; i < 5; i++) {
            this.mockMvc.perform(
                    post(path)
                        .with(new AddServletPathRequestPostProcessor(path))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.generateJson(verifyCodeRequest))
                )
                .andExpect(status().isBadRequest());
        }

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(verifyCodeRequest))
            )
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGenerateCustomCode_CodeTooShort() throws Exception {
        SendCustomCodeRequest sendCustomCodeRequest = new SendCustomCodeRequest();
        sendCustomCodeRequest.setEmail("test9@email.com");
        sendCustomCodeRequest.setCode("1");
        sendCustomCodeRequest.setMaximumAttempts(5);
        sendCustomCodeRequest.setMaximumDurationInMinutes(10);

        String path = "/custom/code/send";

        this.mockMvc.perform(
                post(path)
                    .with(new AddServletPathRequestPostProcessor(path))
                    .header("Authorization", "Bearer " + this.token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(sendCustomCodeRequest))
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }

}
