package com.amazon.sample.carts.services;

import com.amazon.sample.carts.repositories.CartEntity;
import com.amazon.sample.carts.repositories.ItemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public class InMemoryCartService implements CartService {

    private final Map<String, Cart> carts;

    public InMemoryCartService() {
        this.carts = new HashMap<>();
    }

    @Override
    public Cart get(String customerId) {
        if(!this.carts.containsKey(customerId)) {
            Cart cart = new Cart(customerId);

            this.carts.put(customerId, cart);
        }

        return this.carts.get(customerId);
    }

    @Override
    public void delete(String customerId) {
        this.carts.remove(customerId);
    }

    @Override
    public Cart merge(String sessionId, String customerId) {
        return null;
    }

    @Override
    public CartItem add(String customerId, String itemId, int quantity, int unitPrice) {
        CartItem item = new CartItem(itemId, quantity, unitPrice);

        this.get(customerId).getItems().add(item);

        return item;
    }

    @Override
    public List<CartItem> items(String customerId) {
        return this.get(customerId).getItems();
    }

    @Override
    public Optional<CartItem> item(String customerId, String itemId) {
        Cart cart = this.get(customerId);

        for(CartItem item : cart.getItems()) {
            if(item.getItemId().equals(itemId)) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    @Override
    public void deleteItem(String customerId, String itemId) {
        Cart cart = this.get(customerId);

        cart.getItems().removeIf(i -> i.getItemId().equals(itemId));
    }

    @Override
    public Optional<CartItem> update(String customerId, String itemId, int quantity, int unitPrice) {
        Cart cart = this.get(customerId);

        for(CartItem item : cart.getItems()) {
            if(item.getItemId().equals(itemId)) {
                item.setQuantity(quantity);
                item.setUnitPrice(unitPrice);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean exists(String customerId) {
        return this.carts.containsKey(customerId);
    }
}

@Data
class Cart implements CartEntity {

    private String customerId;

    private List<CartItem> items = new ArrayList<>();

    public Cart(String customerId) {
        this.customerId = customerId;
    }
}

@Data
@AllArgsConstructor
class CartItem implements ItemEntity {

    private String itemId;

    private int quantity;

    private int unitPrice;
}