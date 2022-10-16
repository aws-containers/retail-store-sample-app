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
