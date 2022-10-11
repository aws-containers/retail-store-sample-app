package com.amazon.sample.carts.controllers.api;

import com.amazon.sample.carts.repositories.ItemEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String itemId;

    private int quantity;

    private int unitPrice;

    public static Item from(ItemEntity itemEntity) {
        Item item = new Item();
        item.itemId = itemEntity.getItemId();
        item.quantity = itemEntity.getQuantity();
        item.unitPrice = itemEntity.getUnitPrice();

        return item;
    }
}
