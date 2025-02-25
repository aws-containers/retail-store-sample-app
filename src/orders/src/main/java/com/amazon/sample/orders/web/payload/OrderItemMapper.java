package com.amazon.sample.orders.web.payload;

import com.amazon.sample.orders.entities.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
  OrderItem toOrderItem(OrderItemEntity entity);

  OrderItemEntity toOrderItemEntity(OrderItem item);
}
