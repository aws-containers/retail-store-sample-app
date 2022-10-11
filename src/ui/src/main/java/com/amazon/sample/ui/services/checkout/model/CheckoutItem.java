package com.amazon.sample.ui.services.checkout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutItem {
    private String id;

    private String name;

    private Integer quantity;

    private Integer unitCost;

    private Integer totalCost;

    private String imageUrl;
}
