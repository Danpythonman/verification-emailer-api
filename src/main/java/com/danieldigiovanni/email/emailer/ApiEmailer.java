package com.danieldigiovanni.email.emailer;

import com.danieldigiovanni.email.emailer.dto.ApiEmailerRequest;
import com.danieldigiovanni.email.emailer.exception.ApiCallResponseBodyException;
import com.danieldigiovanni.email.emailer.exception.ApiCallStatusException;
import com.danieldigiovanni.email.emailer.exception.InvalidUrlException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Sends an actual email via another API.
 */
public class ApiEmailer implements Emailer {

    private final String fromAddress;
    private final String sendEmailUrl;
    private final String refreshTokenUrl;
    private final String authScheme;
    private final String emailHtmlTemplate;
    private final RestTemplate restTemplate;
    private String accessToken;
    private final Logger log = LoggerFactory.getLogger(ApiEmailer.class);

    public ApiEmailer(String fromAddress, String sendEmailUrl, String refreshTokenUrl, String authScheme, String emailHtmlTemplate, RestTemplate restTemplate) {
        this.fromAddress = fromAddress;
        this.sendEmailUrl = sendEmailUrl;
        this.refreshTokenUrl = refreshTokenUrl;
        this.authScheme = authScheme;
        this.emailHtmlTemplate = emailHtmlTemplate;
        this.restTemplate = restTemplate;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sends an actual email via an API.
     *
     * @throws InvalidUrlException          If the API URL is invalid.
     * @throws ApiCallStatusException       If the API responds with an
     *                                      unexpected status.
     * @throws ApiCallResponseBodyException If the API responds with an
     *                                      unexpected response body.
     */
    @Override
    public void sendEmail(String toAddress, String subject, String code, Integer duration) {
        if (this.accessToken == null) {
            this.log.info("Access token is null, refreshing token");
            this.refreshToken();
        }

        String emailHtmlContent = this.emailHtmlTemplate
            .replace("{{code}}", code)
            .replace("{{duration}}", duration.toString());

        this.log.info("Calling mail API");
        ResponseEntity<JsonNode> response = this.callMailApi(
            toAddress,
            subject,
            emailHtmlContent
        );

        if (response.getStatusCode().is4xxClientError()) {
            this.log.info(
                "Mail API responded with status {}, refreshing token",
                response.getStatusCode().value()
            );
            this.refreshToken();
            this.log.info("Calling mail API again");
            response = this.callMailApi(toAddress, subject, emailHtmlContent);
        }

        if (response.getStatusCode().isError()) {
            throw new ApiCallStatusException(
                "Error calling send mail API",
                response.getStatusCode(),
                response.getBody()
            );
        }
    }

    /**
     * Calls the API to send an email.
     * <p>
     * Note that the property {@code this.accessToken} must be a valid access
     * token for the email sending to succeed.
     *
     * @param toAddress The recipient of the email.
     * @param subject   The subject of the email.
     * @param content   The HTML content of the email.
     *
     * @return The response from the API.
     *
     * @throws InvalidUrlException If the call mail API URL is invalid.
     */
    private ResponseEntity<JsonNode> callMailApi(String toAddress, String subject, String content) {
        URI url;
        try {
            url = new URI(this.sendEmailUrl);
        } catch (URISyntaxException exception) {
            throw new InvalidUrlException("Invalid send email API URL");
        }

        ApiEmailerRequest requestBody = new ApiEmailerRequest(
            this.fromAddress,
            toAddress,
            subject,
            content
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(
            HttpHeaders.AUTHORIZATION,
            this.authScheme + " " + this.accessToken
        );

        HttpEntity<Object> httpEntity = new HttpEntity<>(
            requestBody,
            headers
        );

        return this.restTemplate.exchange(
            url,
            HttpMethod.POST,
            httpEntity,
            JsonNode.class
        );
    }

    /**
     * Refreshes the access token.
     * <p>
     * This works by calling the refresh token API and updating the property
     * {@code this.accessToken} with the access token returned from the API.
     *
     * @throws InvalidUrlException          If the refresh token URL is
     *                                      invalid.
     * @throws ApiCallStatusException       If the refresh token API responds
     *                                      with an unexpected status.
     * @throws ApiCallResponseBodyException If the refresh token API responds
     *                                      with an unexpected response body.
     */
    private void refreshToken() {
        URI url;
        try {
            url = new URI(this.refreshTokenUrl);
        } catch (URISyntaxException exception) {
            throw new InvalidUrlException("Invalid refresh token URL");
        }

        ResponseEntity<JsonNode> response = this.restTemplate.exchange(
            url,
            HttpMethod.POST,
            HttpEntity.EMPTY,
            JsonNode.class
        );

        if (response.getStatusCode().isError()) {
            throw new ApiCallStatusException(
                "Error calling refresh token",
                response.getStatusCode(),
                response.getBody()
            );
        }

        JsonNode responseBody = response.getBody();
        if (responseBody == null) {
            throw new ApiCallResponseBodyException(
                "Invalid refresh token response body: response body is null",
                null
            );
        }

        JsonNode accessTokenNode = responseBody.get("access_token");
        if (accessTokenNode == null) {
            throw new ApiCallResponseBodyException(
                "Invalid refresh token response body: response body does not" +
                    " contain \"access_token\" property",
                responseBody
            );
        }

        this.accessToken = accessTokenNode.asText();
    }

}
