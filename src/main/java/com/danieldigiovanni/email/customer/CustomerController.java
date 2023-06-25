package com.danieldigiovanni.email.customer;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customer")
    public Customer getCustomerById(Principal principal) {
        return this.customerService.getCustomerByPrincipal(principal);
    }

    @PatchMapping("/customer")
    public Customer updateCustomer(Principal principal, @RequestBody Map<String, String> updates) {
        return this.customerService.updateCustomer(principal, updates);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFoundException(EntityNotFoundException exception) {
        return exception.getMessage();
    }

}
