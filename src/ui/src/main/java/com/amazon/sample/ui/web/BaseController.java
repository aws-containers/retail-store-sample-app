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

package com.amazon.sample.ui.web;

import com.amazon.sample.ui.clients.carts.api.CartsApi;
import com.amazon.sample.ui.services.Metadata;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.web.util.SessionIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${retail.ui.banner}")
    protected String bannerText;

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
        model.addAttribute("bannerText", this.bannerText);
        model.addAttribute("metadata", metadata);
    }

    protected String getSessionID(ServerHttpRequest request) {
        return SessionIDUtil.getSessionId(request);
    }
}
