package com.amazon.sample.ui.services.checkout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingOption {
    private String name;

    private Integer amount;

    private String token;

    private Integer estimatedDays;
}
