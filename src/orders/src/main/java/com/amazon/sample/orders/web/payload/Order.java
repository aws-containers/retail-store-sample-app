package com.amazon.sample.orders.web.payload;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String firstName;

    private String lastName;

    private String email;

    private List<OrderItem> items;
}
