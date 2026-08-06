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

import static org.assertj.core.api.Assertions.assertThat;

import com.amazon.sample.ui.services.carts.MockCartsService;
import com.amazon.sample.ui.services.catalog.MockCatalogService;
import com.amazon.sample.ui.services.catalog.model.CatalogMapper;
import com.amazon.sample.ui.services.checkout.MockCheckoutService;
import com.amazon.sample.ui.services.checkout.model.CheckoutMapper;
import com.amazon.sample.ui.services.recommendations.MockRecommendationsService;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StoreServicesTest {

  @Test
  void usesMockServicesWhenEndpointsAreDisabled() {
    var endpoints = new EndpointProperties();
    endpoints.setCatalog("false");
    endpoints.setCarts("false");
    endpoints.setCheckout("false");
    endpoints.setRecommendations("false");

    var storeServices = new StoreServices(endpoints);
    var factory = new OkHttpClient();
    var catalogService = storeServices.catalogService(
      Mappers.getMapper(CatalogMapper.class),
      factory
    );
    var cartsService = storeServices.cartsService(catalogService, factory);
    var checkoutService = storeServices.checkoutService(
      cartsService,
      Mappers.getMapper(CheckoutMapper.class),
      factory
    );
    var recommendationsService = storeServices.recommendationsService(
      catalogService,
      factory
    );

    assertThat(catalogService).isInstanceOf(MockCatalogService.class);
    assertThat(cartsService).isInstanceOf(MockCartsService.class);
    assertThat(checkoutService).isInstanceOf(MockCheckoutService.class);
    assertThat(recommendationsService)
      .isInstanceOf(MockRecommendationsService.class);
  }
}
