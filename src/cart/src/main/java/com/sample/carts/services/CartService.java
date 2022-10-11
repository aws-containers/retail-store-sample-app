package com.amazon.sample.carts.services;

import com.amazon.sample.carts.repositories.CartEntity;
import com.amazon.sample.carts.repositories.ItemEntity;

import java.util.List;
import java.util.Optional;

public interface CartService {
    CartEntity get(String customerId);

    void delete(String customerId);

    CartEntity merge(String sessionId, String customerId);

    ItemEntity add(String customerId, String itemId, int quantity, int unitPrice);

    List<? extends ItemEntity> items(String customerId);

    Optional<? extends ItemEntity> item(String customerId, String itemId);

    void deleteItem(String customerId, String itemId);

    Optional<? extends ItemEntity> update(String customerId, String itemId, int quantity, int unitPrice);

    boolean exists(String customerId);
}
