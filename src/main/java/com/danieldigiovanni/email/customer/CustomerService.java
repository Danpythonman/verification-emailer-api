package com.danieldigiovanni.email.customer;

import com.danieldigiovanni.email.customer.dto.UpdateCustomerRequest;
import com.danieldigiovanni.email.customer.dto.UpdatePasswordRequest;
import com.danieldigiovanni.email.utils.AuthUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
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

    public Customer updateCustomer(Principal principal, UpdateCustomerRequest updates) {
        Customer customer = this.getCustomerByPrincipal(principal);

        customer.setName(updates.getName());
        customer.setUpdatedAt(new Date());

        return this.customerRepository.save(customer);
    }

    public Customer updatePassword(Principal principal, UpdatePasswordRequest updates) {
        Customer customer = this.getCustomerByPrincipal(principal);

        boolean oldPasswordCorrect = this.passwordEncoder.matches(
            updates.getOldPassword(),
            customer.getPassword()
        );
        if (!oldPasswordCorrect) {
            throw new ValidationException("Old password is incorrect");
        }

        boolean newPasswordSameAsOldPassword = this.passwordEncoder.matches(
            updates.getNewPassword(),
            customer.getPassword()
        );
        if (newPasswordSameAsOldPassword) {
            throw new ValidationException(
                "New password cannot be the same as the old password"
            );
        }

        // Throws exception if password does not match constraints
        AuthUtils.checkPasswordValidity(updates.getNewPassword());

        String newHashedPassword = this.passwordEncoder.encode(
            updates.getNewPassword()
        );

        boolean newPasswordsMatch = this.passwordEncoder.matches(
            updates.getConfirmPassword(),
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
