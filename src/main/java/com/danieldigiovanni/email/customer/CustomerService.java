package com.danieldigiovanni.email.customer;

import jakarta.persistence.EntityExistsException;
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

    public Customer saveCustomer(Customer customer) {
        Optional<Customer> customerOptional =
            this.customerRepository.findByEmail(customer.getEmail());

        if (customerOptional.isEmpty()) {
            return this.customerRepository.save(customer);
        }
        throw new EntityExistsException(
            "Customer with email " + customer.getEmail() + " already exists"
        );
    }

    public Customer getCustomerById(Long id) {
        return this.customerRepository.findById(id).get();
    }

}
