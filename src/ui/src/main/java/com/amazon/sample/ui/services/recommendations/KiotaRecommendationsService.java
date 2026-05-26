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

package com.amazon.sample.ui.services.recommendations;

import com.amazon.sample.ui.client.recommendations.RecommendationsClient;
import com.amazon.sample.ui.services.catalog.CatalogService;
import com.amazon.sample.ui.services.catalog.model.Product;
import reactor.core.publisher.Flux;

public class KiotaRecommendationsService implements RecommendationsService {

  private final RecommendationsClient recommendationsClient;
  private final CatalogService catalogService;

  public KiotaRecommendationsService(
    RecommendationsClient recommendationsClient,
    CatalogService catalogService
  ) {
    this.recommendationsClient = recommendationsClient;
    this.catalogService = catalogService;
  }

  @Override
  public Flux<Product> getRecommendations(String productId, int size) {
    return Flux.fromIterable(
      this.recommendationsClient.recommendations().byProductId(productId).get()
    )
      .take(size)
      .flatMap(r -> this.catalogService.getProduct(r.getProductId()));
  }
}
