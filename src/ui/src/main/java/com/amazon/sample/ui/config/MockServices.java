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
