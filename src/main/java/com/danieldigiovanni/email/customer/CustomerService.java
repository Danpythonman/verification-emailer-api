package com.danieldigiovanni.email.customer;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
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

}
