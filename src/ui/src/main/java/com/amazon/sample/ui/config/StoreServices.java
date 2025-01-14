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

import com.amazon.sample.ui.client.cart.CartClient;
import com.amazon.sample.ui.client.catalog.CatalogClient;
import com.amazon.sample.ui.client.checkout.CheckoutClient;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.carts.KiotaCartsService;
import com.amazon.sample.ui.services.carts.MockCartsService;
import com.amazon.sample.ui.services.catalog.CatalogService;
import com.amazon.sample.ui.services.catalog.KiotaCatalogService;
import com.amazon.sample.ui.services.catalog.MockCatalogService;
import com.amazon.sample.ui.services.catalog.model.CatalogMapper;
import com.amazon.sample.ui.services.checkout.CheckoutService;
import com.amazon.sample.ui.services.checkout.KiotaCheckoutService;
import com.amazon.sample.ui.services.checkout.MockCheckoutService;
import com.amazon.sample.ui.services.checkout.model.CheckoutMapper;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.bundle.DefaultRequestAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreServices {

  @Bean
  @ConditionalOnProperty(prefix = "retail.ui.endpoints", name = "catalog")
  public CatalogService catalogService(
    @Value("${retail.ui.endpoints.catalog}") String endpoint,
    CatalogMapper mapper
  ) {
    var requestAdapter = new DefaultRequestAdapter(
      new AnonymousAuthenticationProvider()
    );
    requestAdapter.setBaseUrl(endpoint);

    return new KiotaCatalogService(new CatalogClient(requestAdapter), mapper);
  }

  @Bean
  @ConditionalOnProperty(
    prefix = "retail.ui.endpoints",
    name = "catalog",
    havingValue = "false",
    matchIfMissing = true
  )
  public CatalogService mockCatalogService() {
    return new MockCatalogService();
  }

  @Bean
  @ConditionalOnProperty(prefix = "retail.ui.endpoints", name = "carts")
  public CartsService cartsService(
    @Value("${retail.ui.endpoints.carts}") String endpoint,
    CatalogService catalogService
  ) {
    var requestAdapter = new DefaultRequestAdapter(
      new AnonymousAuthenticationProvider()
    );
    requestAdapter.setBaseUrl(endpoint);

    return new KiotaCartsService(
      new CartClient(requestAdapter),
      catalogService
    );
  }

  @Bean
  @ConditionalOnProperty(
    prefix = "retail.ui.endpoints",
    name = "carts",
    havingValue = "false",
    matchIfMissing = true
  )
  public CartsService mockCartsService(CatalogService catalogService) {
    return new MockCartsService(catalogService);
  }

  @Bean
  @ConditionalOnProperty(prefix = "retail.ui.endpoints", name = "checkout")
  public CheckoutService checkoutService(
    @Value("${retail.ui.endpoints.checkout}") String endpoint,
    CartsService cartsService,
    CheckoutMapper mapper
  ) {
    var requestAdapter = new DefaultRequestAdapter(
      new AnonymousAuthenticationProvider()
    );
    requestAdapter.setBaseUrl(endpoint);

    return new KiotaCheckoutService(
      new CheckoutClient(requestAdapter),
      mapper,
      cartsService
    );
  }

  @Bean
  @ConditionalOnProperty(
    prefix = "retail.ui.endpoints",
    name = "checkout",
    havingValue = "false",
    matchIfMissing = true
  )
  public CheckoutService mockCheckoutService(
    CheckoutMapper mapper,
    CartsService cartsService
  ) {
    return new MockCheckoutService(mapper, cartsService);
  }
}
