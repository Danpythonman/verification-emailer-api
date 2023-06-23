package com.danieldigiovanni.email.customer;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerById(Long id) {
        Optional<Customer> customerOptional
            = this.customerRepository.findById(id);

        if (customerOptional.isPresent()) {
            return customerOptional.get();
        }
        throw new EntityNotFoundException(
            "Customer with ID " + id + " does not exist"
        );
    }

}
