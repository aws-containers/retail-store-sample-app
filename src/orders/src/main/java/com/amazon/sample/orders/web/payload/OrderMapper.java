package com.amazon.sample.orders.web.payload;

import com.amazon.sample.orders.entities.OrderEntity;
import com.amazon.sample.orders.entities.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toOrder(OrderEntity entity);

    OrderItem toOrderItem(OrderItemEntity entity);

    ExistingOrder toExistingOrder(OrderEntity entity);

    OrderEntity toOrderEntity(Order order);

    OrderItemEntity toOrderItemEntity(OrderItem item);
}
