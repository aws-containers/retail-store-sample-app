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

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SessionIDWebFilter implements WebFilter {

  @Override
  public Mono<Void> filter(
    ServerWebExchange serverWebExchange,
    WebFilterChain webFilterChain
  ) {
    String sessionId;

    HttpCookie cookie = serverWebExchange
      .getRequest()
      .getCookies()
      .getFirst(SessionIDUtil.COOKIE_NAME);

    if (cookie == null) {
      sessionId = SessionIDUtil.addSessionCookie(serverWebExchange);
    } else {
      sessionId = cookie.getValue();
    }

    serverWebExchange = serverWebExchange
      .mutate()
      .request(
        serverWebExchange
          .getRequest()
          .mutate()
          .header(SessionIDUtil.HEADER_NAME, sessionId)
          .build()
      )
      .build();

    return webFilterChain.filter(serverWebExchange);
  }
}
