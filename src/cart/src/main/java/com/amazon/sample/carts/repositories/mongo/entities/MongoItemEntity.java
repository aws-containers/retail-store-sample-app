/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
