package com.danieldigiovanni.email.auth;

import com.danieldigiovanni.email.customer.Customer;
import com.danieldigiovanni.email.customer.CustomerRepository;
import com.danieldigiovanni.email.utils.AuthUtils;
import com.danieldigiovanni.email.utils.JwtUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Long TOKEN_DURATION_MILLIS;
    private final String TOKEN_SECRET_KEY;

    @Autowired
    public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, @Value("${token-duration-millis}") Long tokenDurationMillis, @Value("${token-secret-key}") String tokenSecretKey) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.TOKEN_DURATION_MILLIS = tokenDurationMillis;
        this.TOKEN_SECRET_KEY = tokenSecretKey;
    }

    public AuthResponse register(@RequestBody RegisterRequest registerRequest) throws EntityExistsException, ValidationException {
        Optional<Customer> existingCustomer =
            this.customerRepository.findByEmail(registerRequest.getEmail());

        if (existingCustomer.isPresent()) {
            throw new EntityExistsException(
                "Customer with email "
                    + registerRequest.getEmail()
                    + " already exists"
            );
        }

        if (registerRequest.getConfirmPassword() == null) {
            throw new ValidationException("Confirm password is required");
        }
        // Throws exception if password does not match constraints
        AuthUtils.checkPasswordValidity(registerRequest.getPassword());

        String hashedPassword = this.passwordEncoder.encode(
            registerRequest.getPassword()
        );

        if (
            !this.passwordEncoder.matches(
                registerRequest.getConfirmPassword(),
                hashedPassword
            )
        ) {
            throw new ValidationException("Passwords do not match");
        }

        Customer customer = new Customer(
            registerRequest.getName(),
            registerRequest.getEmail(),
            hashedPassword,
            false,
            new Date()
        );

        customer = this.customerRepository.save(customer);

        String jwt = JwtUtils.generateToken(
            customer,
            this.TOKEN_DURATION_MILLIS,
            this.TOKEN_SECRET_KEY
        );

        return new AuthResponse(jwt);
    }

    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        Customer customer = this.customerRepository
            .findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new EntityNotFoundException(
                "Customer with email "
                    + loginRequest.getEmail()
                    + " does not exist"
            ));

        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                Long.toString(customer.getId()),
                loginRequest.getPassword()
            )
        );

        String jwt = JwtUtils.generateToken(
            customer,
            this.TOKEN_DURATION_MILLIS,
            this.TOKEN_SECRET_KEY
        );

        return new AuthResponse(jwt);
    }

}
