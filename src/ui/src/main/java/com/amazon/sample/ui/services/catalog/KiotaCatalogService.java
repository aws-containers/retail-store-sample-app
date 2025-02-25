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

package com.amazon.sample.ui.services.catalog;

import com.amazon.sample.ui.client.catalog.CatalogClient;
import com.amazon.sample.ui.services.catalog.model.CatalogMapper;
import com.amazon.sample.ui.services.catalog.model.Product;
import com.amazon.sample.ui.services.catalog.model.ProductPage;
import com.amazon.sample.ui.services.catalog.model.ProductTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class KiotaCatalogService implements CatalogService {

  private CatalogClient catalogClient;
  private CatalogMapper mapper;

  public KiotaCatalogService(
    CatalogClient catalogClient,
    CatalogMapper mapper
  ) {
    this.catalogClient = catalogClient;
    this.mapper = mapper;
  }

  @Override
  public Mono<ProductPage> getProducts(
    String tag,
    String order,
    int page,
    int size
  ) {
    var response = Mono.just(
      this.catalogClient.catalog()
        .size()
        .get(getRequestConfiguration -> {
          getRequestConfiguration.queryParameters.tags = tag;
        })
    );

    return Flux.fromIterable(
      this.catalogClient.catalog()
        .products()
        .get(getRequestConfiguration -> {
          getRequestConfiguration.queryParameters.order = order;
          getRequestConfiguration.queryParameters.page = page;
          getRequestConfiguration.queryParameters.size = size;
          getRequestConfiguration.queryParameters.tags = tag;
        })
    )
      .map(mapper::product)
      .collectList()
      .zipWith(response, (p, r) -> new ProductPage(page, size, r.getSize(), p));
  }

  @Override
  public Mono<Product> getProduct(String productId) {
    return Mono.just(
      this.catalogClient.catalog().products().byId(productId).get()
    ).map(mapper::product);
  }

  @Override
  public Flux<ProductTag> getTags() {
    return Flux.fromIterable(this.catalogClient.catalog().tags().get()).map(
      mapper::tag
    );
  }
}
