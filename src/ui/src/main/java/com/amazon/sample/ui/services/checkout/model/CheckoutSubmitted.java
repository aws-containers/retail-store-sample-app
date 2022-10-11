package com.amazon.sample.ui.services.checkout.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutSubmitted {
    private String orderId;

    private String customerEmail;
}
