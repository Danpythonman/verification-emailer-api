package com.danieldigiovanni.email.customer;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer saveCustomer(Customer customer) {
        Optional<Customer> existingCustomer =
            this.customerRepository.findByEmail(customer.getEmail());
        if (existingCustomer.isPresent()) {
            throw new EntityExistsException(
                "Customer with email " + customer.getEmail() + " already exists"
            );
        }

        if (customer.getConfirmPassword() == null) {
            throw new ValidationException("Confirm password is required");
        }
        // Throws exception if password does not match constraints
        this.checkPasswordValidity(customer.getPassword());

        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        String hashedPassword = bcrypt.encode(customer.getPassword());

        if (!bcrypt.matches(customer.getConfirmPassword(), hashedPassword)) {
            throw new ValidationException("Passwords do not match");
        }
        customer.setPassword(hashedPassword);

        customer.setIsVerified(false);
        customer.setCreatedAt(new Date());

        return this.customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {
        Optional<Customer> customerOptional = this.customerRepository.findById(id);

        if (customerOptional.isPresent()) {
            return customerOptional.get();
        }
        throw new EntityNotFoundException(
            "Customer with ID " + id + " does not exist"
        );
    }

    /**
     * Throws exception if password constraints are not satisfied.
     * <p>
     * The constraints are:
     * <ul>
     *     <li>6 or more characters</li>
     *     <li>at least one lower case letter</li>
     *     <li>at least one upper case letter</li>
     *     <li>at least one number</li>
     * </ul>
     *
     * @param password The password to be validated.
     * @throws ValidationException
     */
    private void checkPasswordValidity(String password) throws ValidationException {
        final int MINIMUM_PASSWORD_LENGTH = 6;
        List<String> errorMessages = new ArrayList<>();

        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            errorMessages.add(
                "Password length must have minimum "
                    + MINIMUM_PASSWORD_LENGTH
                    + " characters"
            );
        }
        if (!password.matches(".*[0-9].*")) {
            errorMessages.add("Password must have at least 1 number");
        }
        if (!password.matches(".*[a-z].*")) {
            errorMessages.add("Password must have at least 1 lower case letter");
        }
        if (!password.matches(".*[A-Z].*")) {
            errorMessages.add("Password must have at least 1 upper case letter");
        }

        if (!errorMessages.isEmpty()) {
            throw new ValidationException(String.join(", ", errorMessages));
        }
    }

}
