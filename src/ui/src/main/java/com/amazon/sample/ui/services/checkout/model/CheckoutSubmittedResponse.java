package com.amazon.sample.ui.services.checkout.model;

import com.amazon.sample.ui.services.carts.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutSubmittedResponse {
    private CheckoutSubmitted submitted;

    private Cart cart;
}
