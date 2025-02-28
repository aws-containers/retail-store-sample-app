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

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "order_items")
public class OrderItemEntity {

  @Column(value = "product_id")
  private String productId;

  @Column(value = "quantity")
  private int quantity;

  @Column(value = "unit_cost")
  private int unitCost;

  @Column(value = "total_cost")
  private int totalCost;

  public OrderItemEntity() {}

  public OrderItemEntity(
    String productId,
    int quantity,
    int unitCost,
    int totalCost
  ) {
    this.productId = productId;
    this.quantity = quantity;
    this.unitCost = unitCost;
    this.totalCost = totalCost;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public int getUnitCost() {
    return unitCost;
  }

  public void setUnitCost(int unitCost) {
    this.unitCost = unitCost;
  }

  public int getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(int totalCost) {
    this.totalCost = totalCost;
  }

  @Override
  public String toString() {
    return (
      "OrderItemEntity [productId=" +
      productId +
      ", quantity=" +
      quantity +
      ", unitCost=" +
      unitCost +
      ", totalCost=" +
      totalCost +
      "]"
    );
  }
}
