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

package com.amazon.sample.carts.repositories.dynamo.entities;

import com.amazon.sample.carts.repositories.ItemEntity;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
public class DynamoItemEntity implements ItemEntity {

  private String id;

  private String customerId;

  private String itemId;

  private int quantity;

  private int unitPrice;

  public DynamoItemEntity(
    String id,
    String customerId,
    String itemId,
    int quantity,
    int unitPrice
  ) {
    this.id = id;
    this.customerId = customerId;
    this.itemId = itemId;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  public DynamoItemEntity() {}

  @DynamoDbPartitionKey
  public String getId() {
    return id;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = { "idx_global_customerId" })
  public String getCustomerId() {
    return customerId;
  }

  public String getItemId() {
    return itemId;
  }

  public int getQuantity() {
    return quantity;
  }

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
