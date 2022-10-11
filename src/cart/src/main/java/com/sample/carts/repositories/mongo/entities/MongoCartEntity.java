package com.amazon.sample.carts.repositories.mongo.entities;

import com.amazon.sample.carts.repositories.CartEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class MongoCartEntity implements CartEntity {
    @Id
    private String id;

    @DBRef
    private List<MongoItemEntity> items = new ArrayList<>();

    public MongoCartEntity(String customerId) {
        this.id = customerId;
    }

    public MongoCartEntity() {
        this(null);
    }

    public List<MongoItemEntity> contents() {
        return items;
    }

    public MongoCartEntity add(MongoItemEntity item) {
        items.add(item);
        return this;
    }

    public MongoCartEntity remove(MongoItemEntity item) {
        items.remove(item);
        return this;
    }

    public String getCustomerId() {
        return this.id;
    }
}
