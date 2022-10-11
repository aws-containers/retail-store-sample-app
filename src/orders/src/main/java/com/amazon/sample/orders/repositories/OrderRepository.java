package com.amazon.sample.orders.repositories;

import com.amazon.sample.orders.entities.OrderEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderEntity, String> {

}
