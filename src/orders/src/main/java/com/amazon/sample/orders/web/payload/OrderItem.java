package com.amazon.sample.orders.web.payload;

import lombok.Data;

@Data
public class OrderItem {
    private String productId;

    private int quantity;

    private int price;
}
