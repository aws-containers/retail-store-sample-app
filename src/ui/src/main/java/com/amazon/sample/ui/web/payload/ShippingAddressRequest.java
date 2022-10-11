package com.amazon.sample.ui.web.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class ShippingAddressRequest {
    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String address1;

    private String address2;

    @NotEmpty
    private String city;

    @Pattern(regexp = "^\\d{5}$")
    private String zip;

    @Pattern(regexp = "^[A-Z]{2}$")
    private String state;
}
