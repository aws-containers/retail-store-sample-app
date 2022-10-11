package com.amazon.sample.ui.services.catalog;

import com.amazon.sample.ui.services.catalog.model.Product;
import com.amazon.sample.ui.services.catalog.model.ProductPage;
import com.amazon.sample.ui.services.catalog.model.ProductTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogService {
    Mono<ProductPage> getProducts(String tag, String order, int page, int size);

    Mono<Product> getProduct(String productId);

    Flux<ProductTag> getTags();
}
