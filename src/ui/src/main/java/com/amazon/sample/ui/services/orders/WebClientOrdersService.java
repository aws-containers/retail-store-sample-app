package com.amazon.sample.ui.services.orders;

import com.amazon.sample.ui.clients.orders.api.OrdersApi;
import com.amazon.sample.ui.clients.orders.model.OrderItem;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.carts.model.CartItem;
import com.amazon.sample.ui.services.orders.model.Order;
import reactor.core.publisher.Mono;

public class WebClientOrdersService implements OrdersService {

    private OrdersApi ordersApi;

    private CartsService cartsService;

    public WebClientOrdersService(OrdersApi ordersApi, CartsService cartsService) {
        this.ordersApi = ordersApi;
        this.cartsService = cartsService;
    }

    @Override
    public Mono<Order> order(String sessionId, String firstName, String lastName, String email) {
        com.amazon.sample.ui.clients.orders.model.Order createOrderRequest = new com.amazon.sample.ui.clients.orders.model.Order();
        createOrderRequest.setFirstName(firstName);
        createOrderRequest.setLastName(lastName);
        createOrderRequest.setEmail(email);

        return this.cartsService.getCart(sessionId).flatMap(
                cart -> {
                    for(CartItem item : cart.getItems()) {
                        OrderItem orderItem = new OrderItem()
                                .productId(item.getId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice());

                        createOrderRequest.addItemsItem(orderItem);
                    }

                    return this.ordersApi.createOrder(createOrderRequest);
                })
                .map(o -> new Order(o.getId(), o.getEmail()));
    }
}
