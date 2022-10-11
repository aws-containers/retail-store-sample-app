package com.amazon.sample.ui.config;

import com.amazon.sample.ui.clients.orders.api.OrdersApi;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.carts.MockCartsService;
import com.amazon.sample.ui.services.catalog.CatalogService;
import com.amazon.sample.ui.services.catalog.MockCatalogService;
import com.amazon.sample.ui.services.checkout.CheckoutService;
import com.amazon.sample.ui.services.checkout.MockCheckoutService;
import com.amazon.sample.ui.services.checkout.model.CheckoutMapper;
import com.amazon.sample.ui.services.orders.OrdersService;
import com.amazon.sample.ui.services.orders.WebClientOrdersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("stubs")
public class MockServices {

    @Bean
    public CatalogService catalogService() {
        return new MockCatalogService();
    }

    @Bean
    public CartsService cartsService(CatalogService catalogService) {
        return new MockCartsService(catalogService);
    }

    @Bean
    public CheckoutService checkoutService(CartsService cartsService, CheckoutMapper mapper) {
        return new MockCheckoutService(mapper, cartsService);
    }

    @Bean
    public OrdersService ordersService(OrdersApi ordersApi, CartsService cartsService) {
        return new WebClientOrdersService(ordersApi, cartsService);
    }
}
