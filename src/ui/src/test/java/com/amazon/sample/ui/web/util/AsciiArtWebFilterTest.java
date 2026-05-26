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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AsciiArtWebFilterTest {

  private AsciiArtWebFilter filter;

  @BeforeEach
  void setUp() throws Exception {
    filter = new AsciiArtWebFilter();
    filter.init();
  }

  @Test
  void rootWithoutTextHtml_returnsAsciiPayload() {
    MockServerWebExchange exchange = MockServerWebExchange.from(
      MockServerHttpRequest.get("/").header(HttpHeaders.ACCEPT, "*/*").build()
    );

    AtomicBoolean chainCalled = new AtomicBoolean(false);

    StepVerifier.create(
      filter.filter(exchange, ex -> {
        chainCalled.set(true);
        return Mono.empty();
      })
    ).verifyComplete();

    assertThat(chainCalled).isFalse();
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(exchange.getResponse().getHeaders().getContentType()).isEqualTo(
      new MediaType("text", "plain", java.nio.charset.StandardCharsets.UTF_8)
    );

    String body = exchange.getResponse().getBodyAsString().block();
    assertThat(body).contains("INCOMING TRANSMISSION");
    assertThat(body).contains("END OF TRANSMISSION");
  }

  @Test
  void rootWithTextHtml_delegatesToChain() {
    MockServerWebExchange exchange = MockServerWebExchange.from(
      MockServerHttpRequest.get("/")
        .header(HttpHeaders.ACCEPT, MediaType.TEXT_HTML_VALUE)
        .build()
    );

    AtomicBoolean chainCalled = new AtomicBoolean(false);

    StepVerifier.create(
      filter.filter(exchange, ex -> {
        chainCalled.set(true);
        return Mono.empty();
      })
    ).verifyComplete();

    assertThat(chainCalled).isTrue();
    assertThat(exchange.getResponse().getStatusCode()).isNull();
  }

  @Test
  void nonRootWithoutTextHtml_delegatesToChain() {
    MockServerWebExchange exchange = MockServerWebExchange.from(
      MockServerHttpRequest.get("/catalog")
        .header(HttpHeaders.ACCEPT, "*/*")
        .build()
    );

    AtomicBoolean chainCalled = new AtomicBoolean(false);

    StepVerifier.create(
      filter.filter(exchange, ex -> {
        chainCalled.set(true);
        return Mono.empty();
      })
    ).verifyComplete();

    assertThat(chainCalled).isTrue();
    assertThat(exchange.getResponse().getStatusCode()).isNull();
  }
}
