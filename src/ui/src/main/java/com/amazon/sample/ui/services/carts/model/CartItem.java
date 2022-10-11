package com.amazon.sample.ui.services.carts.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItem {
    private String id;

    private int quantity;

    private int price;

    private String name;

    private String imageUrl;

    public int getTotalPrice() {
        return this.quantity * this.price;
    }

    public void addQuantity(int quantity) {
        this.quantity+=quantity;
    }
}
