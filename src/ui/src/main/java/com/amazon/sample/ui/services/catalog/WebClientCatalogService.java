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
