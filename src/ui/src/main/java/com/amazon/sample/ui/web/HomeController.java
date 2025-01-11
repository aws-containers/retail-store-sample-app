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

import com.amazon.sample.ui.services.catalog.CatalogService;
import com.amazon.sample.ui.web.util.RequiresCommonAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiresCommonAttributes
public class HomeController {

  private static final Integer DEFAULT_PAGE = 1;
  private static final Integer DEFAULT_SIZE = 3;

  private CatalogService catalogService;

  public HomeController(@Autowired CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping("/")
  public String index(final Model model, final ServerHttpRequest request) {
    return home(model, request);
  }

  @GetMapping("/home")
  public String home(final Model model, final ServerHttpRequest request) {
    model.addAttribute(
      "catalog",
      this.catalogService.getProducts("", "", DEFAULT_PAGE, DEFAULT_SIZE)
    );

    return "home";
  }
}
