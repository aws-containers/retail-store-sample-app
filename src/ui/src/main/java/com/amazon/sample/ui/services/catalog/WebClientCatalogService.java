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

import com.amazon.sample.ui.clients.catalog.api.CatalogApi;
import com.amazon.sample.ui.clients.catalog.model.ModelCatalogSizeResponse;
import com.amazon.sample.ui.services.catalog.model.Product;
import com.amazon.sample.ui.services.catalog.model.ProductPage;
import com.amazon.sample.ui.services.catalog.model.ProductTag;
import com.amazon.sample.ui.services.catalog.model.CatalogMapper;
import com.amazon.sample.ui.util.RetryUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebClientCatalogService implements CatalogService {

    private CatalogApi catalogApi;
    private CatalogMapper mapper;

    public WebClientCatalogService(CatalogApi catalogApi, CatalogMapper mapper) {
        this.catalogApi = catalogApi;
        this.mapper = mapper;
    }

    @Override
    public Mono<ProductPage> getProducts(String tag, String order, int page, int size) {
        Mono<ModelCatalogSizeResponse> response = catalogApi.catalogueSizeGet(tag)
                .retryWhen(RetryUtils.apiClientRetrySpec("get products size"));

        return catalogApi.catalogueGet(tag, order, page, size)
                .retryWhen(RetryUtils.apiClientRetrySpec("get products"))
                .map(mapper::product)
                .collectList().zipWith(response, (p, r) -> new ProductPage(page, size, r.getSize(), p));
    }

    @Override
    public Mono<Product> getProduct(String productId) {
        return catalogApi.catalogueProductIdGet(productId)
                .retryWhen(RetryUtils.apiClientRetrySpec("get product"))
                .map(mapper::product);
    }

    @Override
    public Flux<ProductTag> getTags() {
        return catalogApi.catalogueTagsGet()
                .retryWhen(RetryUtils.apiClientRetrySpec("get tags"))
                .map(mapper::tag);
    }
}
