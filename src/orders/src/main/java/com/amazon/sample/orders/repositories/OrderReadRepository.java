package com.amazon.sample.orders.repositories;

import com.amazon.sample.orders.entities.OrderEntity;
import org.springframework.data.repository.CrudRepository;

@ReadOnlyRepository
public interface OrderReadRepository extends CrudRepository<OrderEntity, Long> {

}
