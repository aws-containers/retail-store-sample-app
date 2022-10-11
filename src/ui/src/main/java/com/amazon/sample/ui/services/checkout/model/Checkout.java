package com.amazon.sample.ui.services.checkout.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Checkout {
    private List<CheckoutItem> items;

    private String paymentToken;

    private int subtotal;

    private int tax;

    private int shipping;

    private int total;

    private List<ShippingOption> shippingOptions;
}
