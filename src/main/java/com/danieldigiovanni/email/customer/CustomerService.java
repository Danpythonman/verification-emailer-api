package com.danieldigiovanni.email.customer;

import com.danieldigiovanni.email.utils.AuthUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer getCustomerByPrincipal(Principal principal) {
        Long id = Long.valueOf(principal.getName());

        Optional<Customer> customerOptional
            = this.customerRepository.findById(id);

        return customerOptional.orElseThrow(() -> new EntityNotFoundException(
            "Customer with ID " + id + " does not exist"
        ));
    }

    public Customer updateCustomer(Principal principal, Map<String, String> updates) {
        Customer customer = this.getCustomerByPrincipal(principal);

        if (updates.containsKey("email") || updates.containsKey("password")) {
            throw new ValidationException(
                "Email and password fields must be updated through their " +
                    "own endpoints"
            );
        }

        if (updates.containsKey("name")) {
            customer.setName(updates.get("name"));
            customer.setUpdatedAt(new Date());
        } else {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Valid fields to update are: [\"name\"]"
            );
        }

        return this.customerRepository.save(customer);
    }

    public Customer updatePassword(Principal principal, Map<String, String> updates) {
        Customer customer = this.getCustomerByPrincipal(principal);

        if (
            !updates.containsKey("old")
                || !updates.containsKey("new")
                || !updates.containsKey("confirm")
        ) {
            throw new ValidationException(
                "Old password, new password, and new password confirmation " +
                    "must all be provided"
            );
        }

        boolean oldPasswordCorrect = this.passwordEncoder.matches(
            updates.get("old"),
            customer.getPassword()
        );
        if (!oldPasswordCorrect) {
            throw new ValidationException("Old password is incorrect");
        }

        // Throws exception if password does not match constraints
        AuthUtils.checkPasswordValidity(updates.get("new"));

        String newHashedPassword = this.passwordEncoder.encode(
            updates.get("new")
        );
        boolean newPasswordsMatch = this.passwordEncoder.matches(
            updates.get("confirm"),
            newHashedPassword
        );
        if (!newPasswordsMatch) {
            throw new ValidationException(
                "New password and confirmation of new password do not match"
            );
        }

        customer.setPassword(newHashedPassword);
        customer.setUpdatedAt(new Date());

        return this.customerRepository.save(customer);
    }

}
