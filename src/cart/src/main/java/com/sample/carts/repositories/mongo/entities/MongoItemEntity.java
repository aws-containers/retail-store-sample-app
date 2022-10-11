package com.amazon.sample.carts.repositories.mongo.entities;

import com.amazon.sample.carts.repositories.ItemEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class MongoItemEntity implements ItemEntity {
    @Id
    private String id;

    private String itemId;
    private int quantity;
    private int unitPrice;

    public MongoItemEntity(String id, String itemId, int quantity, int unitPrice) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public MongoItemEntity() {
        this(null, "", 1, 0);
    }

    public MongoItemEntity(String itemId) {
        this(null, itemId, 1, 0);
    }

    public MongoItemEntity(MongoItemEntity item, String id) {
        this(id, item.itemId, item.quantity, item.unitPrice);
    }

    public MongoItemEntity(MongoItemEntity item, int quantity) {
        this(item.id(), item.itemId, quantity, item.unitPrice);
    }

    public String id() {
        return id;
    }

    public String itemId() {
        return itemId;
    }

    public int quantity() {
        return quantity;
    }
}
