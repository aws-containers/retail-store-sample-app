package com.amazon.sample.ui.web.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private int subtotal;

    private List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        this.items.add(item);

        this.subtotal += item.getTotalPrice();
    }

    public static Cart from(List<CartItem> items) {
        Cart cart = new Cart();

        for(CartItem item : items) {
            cart.addItem(item);
        }

        return cart;
    }

    public int getTotalPrice() {
        return this.subtotal + this.getShipping();
    }

    public int getShipping() {
        return this.items.size() > 0 ? 10 : 0;
    }
}
