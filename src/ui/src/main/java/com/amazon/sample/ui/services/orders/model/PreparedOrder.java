package com.amazon.sample.ui.services.orders.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreparedOrder {

  private int subtotal;

  private int tax;

  private int shipping;

  private int total;

  private List<OrderItem> items;
}
