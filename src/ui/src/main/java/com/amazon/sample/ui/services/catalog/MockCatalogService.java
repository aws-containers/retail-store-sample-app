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

import com.amazon.sample.ui.services.catalog.model.Product;
import com.amazon.sample.ui.services.catalog.model.ProductPage;
import com.amazon.sample.ui.services.catalog.model.ProductTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

public class MockCatalogService implements CatalogService {

    private Map<String, Product> products;

    public MockCatalogService() {
        products = new HashMap<>();

        for(int i = 1; i < 21; i++) {
            products.put(""+i, new Product(""+i, "product"+i, "product description "+i, 1, "/assets/img/sample_product.jpg", 123, new ArrayList<>()));
        }
    }

    @Override
    public Mono<ProductPage> getProducts(String tag, String order, int page, int size) {
        List<Product> productList = new ArrayList<>(this.products.values());

        int end = page * size;

        if(end > productList.size()) {
            end = productList.size();
        }

        return Mono.just(new ProductPage(page, size, productList.size(), productList.subList((page-1) * size, end)));
    }

    @Override
    public Mono<Product> getProduct(String productId) {
        return Mono.just(this.products.get(productId));
    }

    @Override
    public Flux<ProductTag> getTags() {
        return Flux.just(new ProductTag("smart", "Smart"));
    }
}
