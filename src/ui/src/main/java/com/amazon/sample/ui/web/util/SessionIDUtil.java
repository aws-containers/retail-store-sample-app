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

import java.util.UUID;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

public class SessionIDUtil {

  public static final String COOKIE_NAME = "SESSIONID";

  public static final String HEADER_NAME = "X-Session-ID";

  protected SessionIDUtil() {
    throw new UnsupportedOperationException();
  }

  public static String addSessionCookie(ServerWebExchange exchange) {
    String sessionId = UUID.randomUUID().toString();

    ResponseCookie newCookie = ResponseCookie.from(
      COOKIE_NAME,
      sessionId
    ).build();

    exchange.getResponse().getCookies().add(COOKIE_NAME, newCookie);

    return sessionId;
  }

  public static String getSessionId(ServerHttpRequest request) {
    return request.getHeaders().getFirst(HEADER_NAME);
  }
}
