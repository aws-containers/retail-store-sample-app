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

import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.web.payload.CartChangeRequest;
import com.amazon.sample.ui.web.util.RequiresCommonAttributes;
import com.amazon.sample.ui.web.util.SessionIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/cart")
@Slf4j
@RequiresCommonAttributes
public class CartController {

  private final CartsService cartsService;

  public CartController(@Autowired CartsService cartsService) {
    this.cartsService = cartsService;
  }

  @GetMapping
  public String cart(ServerHttpRequest request, Model model) {
    String sessionId = SessionIDUtil.getSessionId(request);

    model.addAttribute("fullCart", cartsService.getCart(sessionId));

    return "cart";
  }

  @PostMapping
  public Mono<String> add(
    @ModelAttribute CartChangeRequest addRequest,
    ServerHttpRequest request
  ) {
    String sessionId = SessionIDUtil.getSessionId(request);

    return this.cartsService.addItem(
        sessionId,
        addRequest.getProductId(),
        addRequest.getQuantity()
      ).thenReturn("redirect:/cart");
  }

  @PostMapping("/remove")
  public Mono<String> remove(
    @ModelAttribute CartChangeRequest addRequest,
    ServerHttpRequest request
  ) {
    String sessionId = SessionIDUtil.getSessionId(request);

    return this.cartsService.removeItem(
        sessionId,
        addRequest.getProductId()
      ).thenReturn("redirect:/cart");
  }
}
