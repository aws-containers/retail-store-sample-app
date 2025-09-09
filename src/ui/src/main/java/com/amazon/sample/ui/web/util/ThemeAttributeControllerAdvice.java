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

package com.amazon.sample.ui.web.util;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice
public class ThemeAttributeControllerAdvice {

  private static final String COOKIE_THEME_KEY = "theme";

  private static final List<String> THEMES = List.of(
    "default",
    "orange",
    "green",
    "teal"
  );

  @Value("${retail.ui.theme}")
  private String theme;

  private String getTheme(
    ServerHttpRequest request,
    ServerHttpResponse response
  ) {
    String selectedTheme = request.getQueryParams().getFirst("theme");

    if (selectedTheme != null) {
      ResponseCookie cookie = ResponseCookie.from(
        COOKIE_THEME_KEY,
        selectedTheme
      )
        .path("/")
        .build();
      response.addCookie(cookie);
    } else {
      selectedTheme = request.getCookies().getFirst(COOKIE_THEME_KEY) != null
        ? request.getCookies().getFirst(COOKIE_THEME_KEY).getValue()
        : null;
    }

    if (selectedTheme != null) {
      if (THEMES.contains(selectedTheme)) {
        return selectedTheme;
      }
    }

    return this.theme;
  }

  @ModelAttribute("theme")
  public String addTheme(
    ServerHttpRequest request,
    ServerHttpResponse response
  ) {
    return this.getTheme(request, response);
  }
}
