package com.danieldigiovanni.email.customer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customer")
    public Customer saveCustomer(@RequestBody Customer customer) {
        return this.customerService.saveCustomer(customer);
    }

    @GetMapping("/customer/{id}")
    public Customer getCustomerById(@PathVariable("id") Long id) {
        return this.customerService.getCustomerById(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleValidationExceptions(
        ConstraintViolationException exception
    ) {
        return exception.getConstraintViolations()
            .stream()
            .collect(Collectors.toMap(
                constraintViolation -> constraintViolation.getPropertyPath()
                    .toString(),
                ConstraintViolation::getMessage
            ));
    }

}
