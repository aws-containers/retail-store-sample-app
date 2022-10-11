package com.amazon.sample.ui.services.checkout.model;

import lombok.Data;

@Data
public class ShippingAddress {
    private String firstName;

    private String lastName;

    private String email;

    private String address1;

    private String address2;

    private String city;

    private String zip;

    private String state;
}
