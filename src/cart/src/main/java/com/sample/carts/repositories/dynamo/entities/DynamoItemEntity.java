package com.amazon.sample.carts.repositories.dynamo.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazon.sample.carts.repositories.ItemEntity;

@DynamoDBTable(tableName="Items")
public class DynamoItemEntity implements ItemEntity {
    private String id;

    private String customerId;

    private String itemId;

    private int quantity;

    private int unitPrice;

    public DynamoItemEntity(String id, String customerId, String itemId, int quantity, int unitPrice) {
        this.id = id;
        this.customerId = customerId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public DynamoItemEntity() {

    }

    @DynamoDBHashKey
    public String getId() {
        return id;
    }

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "idx_global_customerId")
    public String getCustomerId() {
        return customerId;
    }

    @DynamoDBAttribute
    public String getItemId() {
        return itemId;
    }

    @DynamoDBAttribute
    public int getQuantity() {
        return quantity;
    }

    @DynamoDBAttribute
    public int getUnitPrice() {
        return unitPrice;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCustomerId(String id) {
        this.customerId = id;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }
}
