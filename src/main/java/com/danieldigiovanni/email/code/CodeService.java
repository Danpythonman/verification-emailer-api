package com.danieldigiovanni.email.code;

import com.danieldigiovanni.email.code.dto.SendCodeRequest;
import com.danieldigiovanni.email.code.dto.CodeResponse;
import com.danieldigiovanni.email.code.dto.VerifyCodeRequest;
import com.danieldigiovanni.email.code.exceptions.IncorrectCodeException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CodeService {

    private final CodeRepository codeRepository;
    private final CodeUtils codeUtils;

    @Autowired
    public CodeService(CodeRepository codeRepository, CodeUtils codeUtils) {
        this.codeRepository = codeRepository;
        this.codeUtils = codeUtils;
    }

    public CodeResponse sendCode(SendCodeRequest sendCodeRequest) {
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
            .email(sendCodeRequest.getEmail())
            .hash(hash)
            .createdAt(new Date())
            .maximumAttempts(sendCodeRequest.getMaximumAttempts())
            .maximumDurationInMinutes(
                sendCodeRequest.getMaximumDurationInMinutes())
            .build();

        code = this.codeRepository.save(code);

        // TODO: Send email

        return new CodeResponse(code);
    }

    public CodeResponse verifyCode(VerifyCodeRequest verifyCodeRequest) {
        List<Code> codes = this.codeRepository.getCodesByEmail(
            verifyCodeRequest.getEmail()
        );

        Code code = codes.stream()
            .filter(Code::isActive)
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(
                "No active code found for " + verifyCodeRequest.getEmail()
            ));

        boolean codeMatches = this.codeUtils.matches(
            verifyCodeRequest.getCode(),
            code.getHash()
        );
        if (!codeMatches) {
            code.incrementIncorrectAttempts();
            code = this.codeRepository.save(code);
            throw new IncorrectCodeException(code);
        }

        code.setFulfilledAt(new Date());
        code = this.codeRepository.save(code);

        return new CodeResponse(code);
    }

}
