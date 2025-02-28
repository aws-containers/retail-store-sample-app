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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "orders")
public class OrderEntity {

  @Id
  private String id;

  private LocalDateTime createdDate;

  @Column(value = "order_id")
  private ShippingAddressEntity shippingAddress;

  @MappedCollection(idColumn = "order_id", keyColumn = "product_id")
  private List<OrderItemEntity> items = new ArrayList<>();

  public OrderEntity() {}

  public OrderEntity(
    List<OrderItemEntity> items,
    ShippingAddressEntity shippingAddress
  ) {
    this.items = items;
    this.shippingAddress = shippingAddress;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public List<OrderItemEntity> getItems() {
    return items;
  }

  public void setItems(List<OrderItemEntity> items) {
    this.items = items;
  }

  public OrderEntity addItem(OrderItemEntity item) {
    this.items.add(item);

    return this;
  }

  public ShippingAddressEntity getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(ShippingAddressEntity shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  @Override
  public String toString() {
    return (
      "OrderEntity [id=" +
      id +
      ", createdDate=" +
      createdDate +
      ", shippingAddress=" +
      shippingAddress +
      ", items=" +
      items +
      "]"
    );
  }
}
