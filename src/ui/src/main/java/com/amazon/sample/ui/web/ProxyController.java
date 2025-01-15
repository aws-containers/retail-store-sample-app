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

import com.amazon.sample.ui.config.EndpointProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/proxy")
public class ProxyController {

  @Autowired
  private EndpointProperties endpoints;

  @GetMapping("/catalog/**")
  public Mono<ResponseEntity<byte[]>> catalogProxy(ProxyExchange<byte[]> proxy)
    throws Exception {
    return doProxy(proxy, "catalog", this.endpoints.getCatalog());
  }

  @GetMapping("/carts/**")
  public Mono<ResponseEntity<byte[]>> cartsProxy(ProxyExchange<byte[]> proxy)
    throws Exception {
    return doProxy(proxy, "carts", this.endpoints.getCarts());
  }

  @GetMapping("/checkout/**")
  public Mono<ResponseEntity<byte[]>> checkoutProxy(
    ProxyExchange<byte[]> proxy
  ) throws Exception {
    return doProxy(proxy, "checkout", this.endpoints.getCheckout());
  }

  @GetMapping("/orders/**")
  public Mono<ResponseEntity<byte[]>> ordersProxy(ProxyExchange<byte[]> proxy)
    throws Exception {
    return doProxy(proxy, "orders", this.endpoints.getOrders());
  }

  public Mono<ResponseEntity<byte[]>> doProxy(
    ProxyExchange<byte[]> proxy,
    String service,
    String endpoint
  ) throws Exception {
    if (isEmpty(endpoint)) {
      return Mono.just(
        new ResponseEntity<>(
          ("Endpoint not provided for " + service).getBytes(),
          HttpStatus.NOT_FOUND
        )
      );
    }

    String path = proxy.path("/proxy");
    return proxy
      .uri(endpoint + path)
      .header("Content-Type", "application/json")
      .forward();
  }

  private boolean isEmpty(String check) {
    return check.equals("false");
  }
}
