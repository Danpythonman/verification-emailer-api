package com.danieldigiovanni.email.code;

import com.danieldigiovanni.email.code.dto.CodeResponse;
import com.danieldigiovanni.email.code.dto.SendCodeRequest;
import com.danieldigiovanni.email.code.dto.VerifyCodeRequest;
import com.danieldigiovanni.email.code.exception.NotYourCodeException;
import com.danieldigiovanni.email.customer.Customer;
import com.danieldigiovanni.email.customer.CustomerService;
import com.danieldigiovanni.email.emailer.Emailer;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Service
public class CodeService {

    private final CodeRepository codeRepository;
    private final CodeUtils codeUtils;
    private final Emailer emailer;
    private final CustomerService customerService;

    @Autowired
    public CodeService(CodeRepository codeRepository, CodeUtils codeUtils, Emailer emailer, CustomerService customerService) {
        this.codeRepository = codeRepository;
        this.codeUtils = codeUtils;
        this.emailer = emailer;
        this.customerService = customerService;
    }

    public CodeResponse sendCode(Principal principal, SendCodeRequest sendCodeRequest) {
        Customer customer =
            this.customerService.getCustomerByPrincipal(principal);

        List<Code> codes = this.codeRepository.getCodesByEmail(
            sendCodeRequest.getEmail()
        );

        boolean emailHasActiveCode = codes.stream().anyMatch(Code::isActive);
        if (emailHasActiveCode) {
            throw new EntityExistsException(
                "Email " + sendCodeRequest.getEmail() + " already has an "
                    + "active code."
            );
        }

        String randomCode = this.codeUtils.generateRandomCode(
            sendCodeRequest.getLength()
        );

        String hash = this.codeUtils.generateHash(randomCode);

        Code code = Code.builder()
            .customer(customer)
            .email(sendCodeRequest.getEmail())
            .hash(hash)
            .createdAt(new Date())
            .maximumAttempts(sendCodeRequest.getMaximumAttempts())
            .maximumDurationInMinutes(
                sendCodeRequest.getMaximumDurationInMinutes())
            .build();

        code = this.codeRepository.save(code);

        this.emailer.sendEmail(
            code.getEmail(),
            "Verification Code",
            randomCode,
            code.getMaximumDurationInMinutes()
        );

        return new CodeResponse(code);
    }

    public ResponseEntity<CodeResponse> verifyCode(Principal principal, VerifyCodeRequest verifyCodeRequest) {
        Customer customer =
            this.customerService.getCustomerByPrincipal(principal);

        List<Code> codes = this.codeRepository.getCodesByEmail(
            verifyCodeRequest.getEmail()
        );

        Code code = codes.stream()
            .filter(Code::isActive)
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(
                "No active code found for " + verifyCodeRequest.getEmail()
            ));

        if (!customer.getId().equals(code.getCustomer().getId())) {
            throw new NotYourCodeException();
        }

        boolean codeMatches = this.codeUtils.matches(
            verifyCodeRequest.getCode(),
            code.getHash()
        );

        if (codeMatches) {
            code.setFulfilledAt(new Date());
            this.codeRepository.save(code);
            return ResponseEntity.noContent().build();
        } else {
            code.incrementIncorrectAttempts();
            code = this.codeRepository.save(code);
            return ResponseEntity.badRequest().body(new CodeResponse(code));
        }
    }

}
