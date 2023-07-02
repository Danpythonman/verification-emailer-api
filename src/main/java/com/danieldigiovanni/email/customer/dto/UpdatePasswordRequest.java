package com.danieldigiovanni.email.customer.dto;

import com.danieldigiovanni.email.customer.Customer;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for updating the password field of a {@link Customer}.
 * <p>
 * This requires the old password, the new password, and the confirmation of
 * the new password.
 */
public class UpdatePasswordRequest {

    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String confirmPassword;

    public String getOldPassword() {
        return this.oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
