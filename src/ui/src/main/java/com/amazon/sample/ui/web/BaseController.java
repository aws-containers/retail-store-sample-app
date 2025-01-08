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

import com.amazon.sample.ui.services.Metadata;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.web.util.SessionIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.ui.Model;

@Slf4j
public class BaseController {

  private CartsService cartsService;

  private Metadata metadata;

  @Value("${retail.ui.banner}")
  private String bannerText;

  public BaseController(CartsService cartsService, Metadata metadata) {
    this.cartsService = cartsService;
    this.metadata = metadata;
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

  protected CartsService getCartsService() {
    return cartsService;
  }
}
