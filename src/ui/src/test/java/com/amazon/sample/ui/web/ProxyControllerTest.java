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

import static org.assertj.core.api.Assertions.assertThat;

import com.amazon.sample.ui.config.EndpointProperties;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

class ProxyControllerTest {

  @Test
  void returnsNotFoundWhenEndpointIsMissing() throws Exception {
    var controller = new ProxyController(new EndpointProperties());

    StepVerifier.create(controller.doProxy(null, "catalog", null))
      .assertNext(response -> {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(new String(response.getBody(), StandardCharsets.UTF_8))
          .isEqualTo("Endpoint not provided for catalog");
      })
      .verifyComplete();
  }

  @Test
  void returnsNotFoundWhenEndpointIsDisabled() throws Exception {
    var controller = new ProxyController(new EndpointProperties());

    StepVerifier.create(controller.doProxy(null, "catalog", "false"))
      .assertNext(response -> {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(new String(response.getBody(), StandardCharsets.UTF_8))
          .isEqualTo("Endpoint not provided for catalog");
      })
      .verifyComplete();
  }
}
