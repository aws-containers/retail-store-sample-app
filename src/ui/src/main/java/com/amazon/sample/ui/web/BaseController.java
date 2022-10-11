package com.amazon.sample.ui.web;

import com.amazon.sample.ui.clients.carts.api.CartsApi;
import com.amazon.sample.ui.services.Metadata;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.web.util.SessionIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.ui.Model;
import org.springframework.web.server.ServerWebExchange;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Slf4j
public class BaseController {

    protected CartsService cartsService;

    protected Metadata metadata;

    public BaseController(CartsService cartsService, Metadata metadata) {
        this.cartsService = cartsService;
        this.metadata = metadata;
    }

    protected static RetryBackoffSpec retrySpec(String path) {
        return Retry
            .backoff(3, Duration.ofSeconds(1))
            .doBeforeRetry(context -> log.warn("Retrying {}", path));
    }

    protected void populateCommon(ServerHttpRequest request, Model model) {
        this.populateCart(request, model);
        this.populateMetadata(model);
    }

    protected void populateCart(ServerHttpRequest request, Model model) {
        String sessionId = getSessionID(request);

        model.addAttribute("cart", this.cartsService.getCart(sessionId));
    }

    protected void populateMetadata(Model model) {
        model.addAttribute("metadata", metadata);
    }

    protected String getSessionID(ServerHttpRequest request) {
        return SessionIDUtil.getSessionId(request);
    }
}
