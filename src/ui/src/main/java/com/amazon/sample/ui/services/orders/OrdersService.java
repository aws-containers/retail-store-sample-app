package com.amazon.sample.ui.services.orders;

import com.amazon.sample.ui.services.orders.model.Order;
import reactor.core.publisher.Mono;

public interface OrdersService {
    Mono<Order> order(String sessionId, String firstName, String lastName, String email);
}
