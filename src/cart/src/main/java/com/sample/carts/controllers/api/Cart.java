package com.amazon.sample.carts.controllers.api;

import com.amazon.sample.carts.repositories.CartEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Cart {
    private String customerId;

    private List<Item> items = new ArrayList<>();

    public static Cart from(CartEntity cartEntity) {
        Cart cart = new Cart();
        cart.setCustomerId(cartEntity.getCustomerId());

        cart.items = cartEntity.getItems().stream()
                .map(Item::from).collect(Collectors.toList());

        return cart;
    }
}
