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
