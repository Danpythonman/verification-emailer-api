package com.danieldigiovanni.email.auth;

import com.danieldigiovanni.email.customer.Customer;
import com.danieldigiovanni.email.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerDetails loadUserByUsername(String username) throws UsernameNotFoundException, NumberFormatException {
        Long customerId = Long.valueOf(username);

        Customer customer = this.customerRepository.findById(customerId)
            .orElseThrow(() ->
                new UsernameNotFoundException("Customer not found")
            );

        return new CustomerDetails(
            customer.getId().toString(),
            customer.getPassword()
        );
    }

}
