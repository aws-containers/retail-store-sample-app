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

import com.amazon.sample.ui.services.catalog.CatalogService;
import com.amazon.sample.ui.services.catalog.model.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;

public class MockRecommendationsService implements RecommendationsService {

  private static final int CANDIDATE_POOL_SIZE = 24;

  private final CatalogService catalogService;

  public MockRecommendationsService(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @Override
  public Flux<Product> getRecommendations(String productId, int size) {
    return catalogService
      .getProducts("", "", 1, CANDIDATE_POOL_SIZE)
      .flatMapMany(page -> {
        List<Product> candidates = new ArrayList<>(
          page
            .getProducts()
            .stream()
            .filter(p -> !p.getId().equals(productId))
            .collect(Collectors.toList())
        );
        Collections.shuffle(candidates);
        return Flux.fromIterable(
          candidates.subList(0, Math.min(size, candidates.size()))
        );
      });
  }
}
