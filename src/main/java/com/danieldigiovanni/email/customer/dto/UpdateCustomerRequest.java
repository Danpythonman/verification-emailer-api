package com.danieldigiovanni.email.customer.dto;

import com.danieldigiovanni.email.customer.Customer;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for updating a {@link Customer}.
 * <p>
 * The only available field to update is
 * the customer's name because email and password require additional validation
 * to update, and the other customer fields cannot be updated by the customer
 * themselves.
 */
public class UpdateCustomerRequest {

    @NotNull
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
