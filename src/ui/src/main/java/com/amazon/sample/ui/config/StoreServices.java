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
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.bundle.DefaultRequestAdapter;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.okhttp.v3_0.OkHttpTelemetry;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class StoreServices {

  @Autowired
  private EndpointProperties endpoints;

  public RequestAdapter getRequestAdapter(
    String endpoint,
    Call.Factory factory
  ) {
    var adapter = new DefaultRequestAdapter(
      new AnonymousAuthenticationProvider(),
      null,
      null,
      factory
    );

    adapter.setBaseUrl(endpoint);

    return adapter;
  }

  @Bean
  public Call.Factory tracedClientFactory(OpenTelemetry openTelemetry) {
    return OkHttpTelemetry.builder(openTelemetry)
      .build()
      .newCallFactory(new OkHttpClient.Builder().build());
  }

  @Bean
  public CatalogService catalogService(
    CatalogMapper mapper,
    Call.Factory factory
  ) {
    if (StringUtils.hasText(this.endpoints.getCatalog())) {
      return new KiotaCatalogService(
        new CatalogClient(
          getRequestAdapter(this.endpoints.getCatalog(), factory)
        ),
        mapper
      );
    }

    return new MockCatalogService();
  }

  @Bean
  public CartsService cartsService(
    CatalogService catalogService,
    Call.Factory factory
  ) {
    if (StringUtils.hasText(this.endpoints.getCarts())) {
      return new KiotaCartsService(
        new CartClient(getRequestAdapter(this.endpoints.getCarts(), factory)),
        catalogService
      );
    }

    return new MockCartsService(catalogService);
  }

  @Bean
  public CheckoutService checkoutService(
    CartsService cartsService,
    CheckoutMapper mapper,
    Call.Factory factory
  ) {
    if (StringUtils.hasText(this.endpoints.getCheckout())) {
      return new KiotaCheckoutService(
        new CheckoutClient(
          getRequestAdapter(this.endpoints.getCheckout(), factory)
        ),
        mapper,
        cartsService
      );
    }

    return new MockCheckoutService(mapper, cartsService);
  }
}
