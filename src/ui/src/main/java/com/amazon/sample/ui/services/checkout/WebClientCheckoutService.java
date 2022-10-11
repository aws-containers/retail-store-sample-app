package com.amazon.sample.ui.services.checkout;

import com.amazon.sample.ui.clients.checkout.api.CheckoutApi;
import com.amazon.sample.ui.clients.checkout.model.CheckoutRequest;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.carts.model.Cart;
import com.amazon.sample.ui.services.checkout.model.Checkout;
import com.amazon.sample.ui.services.checkout.model.CheckoutMapper;
import com.amazon.sample.ui.services.checkout.model.CheckoutSubmittedResponse;
import com.amazon.sample.ui.services.checkout.model.ShippingAddress;
import com.amazon.sample.ui.util.RetryUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
public class WebClientCheckoutService implements CheckoutService {

    private final CheckoutApi api;
    private final CheckoutMapper mapper;
    private final CartsService cartsService;

    public WebClientCheckoutService(CheckoutApi api, CheckoutMapper mapper, CartsService cartsService) {
        this.api = api;
        this.mapper = mapper;

        this.cartsService = cartsService;
    }

    @Override
    public Mono<Checkout> get(String sessionId) {
        return api.checkoutControllerGetCheckout(sessionId)
            .retryWhen(RetryUtils.apiClientRetrySpec("get checkout"))
            .map(mapper::checkout);
    }

    @Override
    public Mono<Checkout> create(String sessionId) {
        Mono<Cart> cart = cartsService.getCart(sessionId);

        return cart.flatMap(c -> {
            CheckoutRequest request = new CheckoutRequest();
            request.subtotal(c.getSubtotal());
            request.setItems(c.getItems().stream().map(mapper::fromCartItem).collect(Collectors.toList()));

            return api.checkoutControllerUpdateCheckout(sessionId, request)
                .map(mapper::checkout);
        });
    }

    @Override
    public Mono<Checkout> shipping(String sessionId, String customerEmail, ShippingAddress shippingAddress) {
        return api.checkoutControllerGetCheckout(sessionId).flatMap(c -> {
            CheckoutRequest request = c.getRequest();
            request.setCustomerEmail(customerEmail);
            request.setShippingAddress(this.mapper.clientShippingAddress(shippingAddress));

            return api.checkoutControllerUpdateCheckout(sessionId, request)
                .map(mapper::checkout);
        });
    }

    @Override
    public Mono<Checkout> delivery(String sessionId, String token) {
        return api.checkoutControllerGetCheckout(sessionId).flatMap(c -> {
            CheckoutRequest request = c.getRequest();
            request.setDeliveryOptionToken(token);

            return api.checkoutControllerUpdateCheckout(sessionId, request)
                .map(mapper::checkout);
        });
    }

    @Override
    public Mono<CheckoutSubmittedResponse> submit(String sessionId) {
        return api.checkoutControllerSubmitCheckout(sessionId)
            .zipWith(this.cartsService.deleteCart(sessionId), (e, f) -> {
                log.error("{}", e);
                return new CheckoutSubmittedResponse(mapper.submitted(e), f);
            });
    }
}
