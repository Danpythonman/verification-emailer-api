package com.danieldigiovanni.email.customer;

import com.danieldigiovanni.email.customer.dto.UpdateCustomerRequest;
import com.danieldigiovanni.email.customer.dto.UpdatePasswordRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customer")
    public Customer getCustomerById(Principal principal) {
        return this.customerService.getCustomerByPrincipal(principal);
    }

    @PatchMapping("/customer")
    public Customer updateCustomer(Principal principal, @RequestBody @Valid UpdateCustomerRequest updates) {
        return this.customerService.updateCustomer(principal, updates);
    }

    @PutMapping("/customer/password")
    public Customer updatePassword(Principal principal, @RequestBody @Valid UpdatePasswordRequest updates) {
        return this.customerService.updatePassword(principal, updates);
    }

    @DeleteMapping("/customer")
    public void deleteCustomer(Principal principal) {
        this.customerService.deleteCustomer(principal);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFoundException(EntityNotFoundException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException exception) {
        return exception.getMessage();
    }

}
