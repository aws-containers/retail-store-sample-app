package com.amazon.sample.ui.config;

import com.amazon.sample.ui.clients.carts.api.CartsApi;
import com.amazon.sample.ui.clients.carts.api.ItemsApi;
import com.amazon.sample.ui.clients.catalog.api.CatalogApi;
import com.amazon.sample.ui.clients.checkout.api.CheckoutApi;
import com.amazon.sample.ui.clients.orders.api.OrdersApi;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.carts.WebClientCartsService;
import com.amazon.sample.ui.services.catalog.CatalogService;
import com.amazon.sample.ui.services.catalog.WebClientCatalogService;
import com.amazon.sample.ui.services.catalog.model.CatalogMapper;
import com.amazon.sample.ui.services.checkout.CheckoutService;
import com.amazon.sample.ui.services.checkout.WebClientCheckoutService;
import com.amazon.sample.ui.services.checkout.model.CheckoutMapper;
import com.amazon.sample.ui.services.orders.OrdersService;
import com.amazon.sample.ui.services.orders.WebClientOrdersService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!stubs")
public class WebClientServices {

    @Bean
    public CatalogService catalogService(CatalogApi catalogApi, CatalogMapper mapper) {
        return new WebClientCatalogService(catalogApi, mapper);
    }

    @Bean
    public CartsService cartsService(CartsApi cartsApi, ItemsApi itemsApi, CatalogService catalogService) {
        return new WebClientCartsService(cartsApi, itemsApi, catalogService);
    }

    @Bean
    public CheckoutService checkoutService(CheckoutApi api, CheckoutMapper mapper, CartsService cartsService) {
        return new WebClientCheckoutService(api, mapper, cartsService);
    }

    @Bean
    public OrdersService ordersService(OrdersApi ordersApi, CartsService cartsService) {
        return new WebClientOrdersService(ordersApi, cartsService);
    }
}
