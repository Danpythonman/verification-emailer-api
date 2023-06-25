package com.danieldigiovanni.email.customer;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
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

}
