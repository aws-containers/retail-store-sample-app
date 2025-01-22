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

package com.amazon.sample.events.orders;

import com.amazon.sample.orders.entities.OrderItemEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

/**
 * Order
 * <p>
 * An order within the store
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "firstName", "lastName", "email", "orderItems" })
public class Order {

  /**
   *
   * (Required)
   *
   */
  @JsonProperty("id")
  private String id;

  @JsonProperty("orderItems")
  private List<OrderItemEntity> orderItems;

  @JsonProperty("orderItems")
  public void setOrderItems(List<OrderItemEntity> orderItems) {
    this.orderItems = orderItems;
  }

  @JsonProperty("orderItems")
  public List<OrderItemEntity> getOrderItems() {
    return orderItems;
  }

  /**
   *
   * (Required)
   *
   */
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  /**
   *
   * (Required)
   *
   */
  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb
      .append(Order.class.getName())
      .append('@')
      .append(Integer.toHexString(System.identityHashCode(this)))
      .append('[');
    sb.append("id");
    sb.append('=');
    sb.append(((this.id == null) ? "<null>" : this.id));
    sb.append(',');
    if (sb.charAt((sb.length() - 1)) == ',') {
      sb.setCharAt((sb.length() - 1), ']');
    } else {
      sb.append(']');
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result =
      prime * result + ((orderItems == null) ? 0 : orderItems.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Order other = (Order) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    if (orderItems == null) {
      if (other.orderItems != null) return false;
    } else if (!orderItems.equals(other.orderItems)) return false;
    return true;
  }
}
