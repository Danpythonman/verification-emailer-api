package com.danieldigiovanni.email.code;

import com.danieldigiovanni.email.code.dto.CodeResponse;
import com.danieldigiovanni.email.code.dto.SendCodeRequest;
import com.danieldigiovanni.email.code.dto.SendCustomCodeRequest;
import com.danieldigiovanni.email.code.dto.VerifyCodeRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class CodeController {

    private final CodeService codeService;

    @Autowired
    public CodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    @PostMapping("/code/send")
    public CodeResponse sendCode(Principal principal, @RequestBody @Valid SendCodeRequest sendCodeRequest) {
        return this.codeService.sendCode(principal, sendCodeRequest);
    }

    @PostMapping("/code/verify")
    public ResponseEntity<CodeResponse> verifyCode(Principal principal, @RequestBody @Valid VerifyCodeRequest verifyCodeRequest) {
        return this.codeService.verifyCode(principal, verifyCodeRequest);
    }

    @PostMapping("/custom/code/send")
    public CodeResponse sendCustomCode(Principal principal, @RequestBody @Valid SendCustomCodeRequest sendCustomCodeRequest) {
        return this.codeService.sendCustomCode(principal, sendCustomCodeRequest);
    }

}
