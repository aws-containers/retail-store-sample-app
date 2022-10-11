package com.amazon.sample.ui.services.carts.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        boolean existing = false;

        for(CartItem i : items) {
            if(i.getId().equals(item.getId())) {
                i.addQuantity(item.getQuantity());
                existing = true;
            }
        }

        if(!existing) {
            this.items.add(item);
        }
    }

    public void removeItem(String id) {
        for(CartItem i : items) {
            if(i.getId().equals(id)) {
                this.items.remove(i);
            }
        }
    }

    public int getSubtotal() {
        int subtotal = 0;

        for(CartItem i : items) {
            subtotal += i.getTotalPrice();
        }

        return subtotal;
    }

    public int getTotalPrice() {
        return this.getSubtotal() + this.getShipping();
    }

    public int getShipping() {
        return this.items.size() > 0 ? 10 : 0;
    }

    public int getNumItems() {
        int total = 0;

        for(CartItem i : items) {
            total += i.getQuantity();
        }

        return total;
    }
}
