package com.amazon.sample.ui.services.orders.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
    private String id;

    private String email;
}
