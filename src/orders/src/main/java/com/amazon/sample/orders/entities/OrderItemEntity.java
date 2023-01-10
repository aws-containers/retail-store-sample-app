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

package com.amazon.sample.orders.entities;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="CUSTOMER_ORDER_ITEM")
@Data
public class OrderItemEntity {
    @Embeddable
    @Data
    public static class Key implements Serializable {
        private String orderId;
        private String productId;
    }

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name="productId",
          column=@Column(length=64))
    })
    private Key id;

    @Transient
    private String productId;

    private int quantity;

    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    private OrderEntity order;
}
