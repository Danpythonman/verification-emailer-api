package com.danieldigiovanni.email.code;

import com.danieldigiovanni.email.code.dto.SendCodeRequest;
import com.danieldigiovanni.email.code.dto.VerifyCodeRequest;
import com.danieldigiovanni.email.code.exceptions.IncorrectCodeException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CodeService {

    private final CodeRepository codeRepository;
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Autowired
    public CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public Object sendCode(SendCodeRequest sendCodeRequest) {
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

        String randomCode = new SecureRandom()
            .ints(sendCodeRequest.getLength(), 0, 10)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining());

        String hash = bcrypt.encode(randomCode);

        Code code = Code.builder()
            .email(sendCodeRequest.getEmail())
            .hash(hash)
            .createdAt(new Date())
            .maximumAttempts(sendCodeRequest.getMaximumAttempts())
            .maximumDurationInMinutes(
                sendCodeRequest.getMaximumDurationInMinutes())
            .build();

        this.codeRepository.save(code);

        // TODO: Send email

        return Map.of(
            "code", randomCode,
            "hash", code.getHash(),
            "incorrectAttempts", code.getIncorrectAttempts(),
            "maxAttempts", code.getMaximumAttempts(),
            "maxDuration", code.getMaximumDurationInMinutes()
        );
    }

    public Object verifyCode(VerifyCodeRequest verifyCodeRequest) {
        List<Code> codes = this.codeRepository.getCodesByEmail(
            verifyCodeRequest.getEmail()
        );

        Code code = codes.stream()
            .filter(Code::isActive)
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(
                "No active code found for " + verifyCodeRequest.getEmail()
            ));

        boolean codeMatches = this.bcrypt.matches(
            verifyCodeRequest.getCode(),
            code.getHash()
        );
        if (!codeMatches) {
            code.incrementIncorrectAttempts();
            this.codeRepository.save(code);
            throw new IncorrectCodeException();
        }

        code.setFulfilledAt(new Date());
        this.codeRepository.save(code);

        return "Success";
    }

}
