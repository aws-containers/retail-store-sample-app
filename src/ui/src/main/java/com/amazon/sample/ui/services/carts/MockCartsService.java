package com.amazon.sample.ui.services.carts;

import com.amazon.sample.ui.services.carts.model.Cart;
import com.amazon.sample.ui.services.carts.model.CartItem;
import com.amazon.sample.ui.services.catalog.CatalogService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import reactor.core.publisher.Mono;

public class MockCartsService implements CartsService {

    private CatalogService catalogService;

    private Map<String, Cart> carts;

    public MockCartsService(CatalogService catalogService) {
            this.catalogService = catalogService;

        this.carts = new HashMap<>();
    }

    @Override
    public Mono<Cart> getCart(String sessionId) {
        return Mono.just(getOrCreate(sessionId));
    }

    private Cart getOrCreate(String sessionId) {
        Cart cart;

        if (!carts.containsKey(sessionId)) {
            cart = this.create(sessionId);
        } else {
            cart = carts.get(sessionId);
        }

        return cart;
    }

    private Cart create(String sessionId) {
        Cart cart = new Cart(new ArrayList<>());

        this.carts.put(sessionId, cart);

        return cart;
    }

    @Override
    public Mono<Cart> deleteCart(String sessionId) {
        return Mono.just(this.create(sessionId));
    }

    @Override
    public Mono<Void> addItem(String sessionId, String productId) {
        Cart cart = getOrCreate(sessionId);

        return this.catalogService.getProduct(productId)
                .map(p -> new CartItem(productId, 1,
                    p.getPrice(),
                    p.getName(),
                    p.getImageUrl()))
                .doOnNext(i -> cart.addItem(i)).then();
    }

    @Override
    public Mono<Void> removeItem(String sessionId, String productId) {
        getOrCreate(sessionId).removeItem(productId);

        return Mono.empty();
    }
}
