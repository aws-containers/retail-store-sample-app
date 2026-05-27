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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class TopologyServiceTest {

  private static final String ENDPOINT = "http://service.test";

  @Test
  void returnsNoneWhenEndpointIsNull() {
    var recorder = new RequestRecorder(req -> response(HttpStatus.OK, ""));
    var service = new TopologyService(buildClient(recorder));

    StepVerifier.create(service.getTopologyForService("svc", null))
      .assertNext(info -> {
        assertThat(info.getStatus()).isEqualTo(TopologyStatus.NONE);
        assertThat(info.getServiceName()).isEqualTo("svc");
      })
      .verifyComplete();

    assertThat(recorder.requestedPaths).isEmpty();
  }

  @Test
  void returnsNoneWhenEndpointIsEmpty() {
    var recorder = new RequestRecorder(req -> response(HttpStatus.OK, ""));
    var service = new TopologyService(buildClient(recorder));

    StepVerifier.create(service.getTopologyForService("svc", ""))
      .assertNext(info ->
        assertThat(info.getStatus()).isEqualTo(TopologyStatus.NONE)
      )
      .verifyComplete();

    assertThat(recorder.requestedPaths).isEmpty();
  }

  @Test
  void healthyWithMetadataWhenTopologySucceeds() {
    var recorder = new RequestRecorder(req ->
      jsonResponse(
        HttpStatus.OK,
        "{\"persistenceProvider\":\"mysql\",\"databaseEndpoint\":\"db:3306\"}"
      )
    );
    var service = new TopologyService(buildClient(recorder));

    StepVerifier.create(service.getTopologyForService("svc", ENDPOINT))
      .assertNext(info -> {
        assertThat(info.getStatus()).isEqualTo(TopologyStatus.HEALTHY);
        assertThat(info.hasMetadata()).isTrue();
        assertThat(info.getMetadata())
          .containsEntry("persistenceProvider", "mysql")
          .containsEntry("databaseEndpoint", "db:3306");
      })
      .verifyComplete();

    assertThat(recorder.requestedPaths).containsExactly("/topology");
  }

  @Test
  void fallsBackToHealthWhenTopologyFails() {
    var recorder = new RequestRecorder(req -> {
      String path = req.url().getPath();
      if (path.equals("/topology")) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "");
      }
      if (path.equals("/health")) {
        return response(HttpStatus.OK, "");
      }
      return response(HttpStatus.NOT_FOUND, "");
    });
    var service = new TopologyService(buildClient(recorder));

    StepVerifier.create(service.getTopologyForService("svc", ENDPOINT))
      .assertNext(info -> {
        assertThat(info.getStatus()).isEqualTo(TopologyStatus.HEALTHY);
        assertThat(info.hasMetadata()).isFalse();
      })
      .verifyComplete();

    assertThat(recorder.requestedPaths).containsExactly("/topology", "/health");
  }

  @Test
  void fallsBackToActuatorHealthWhenHealthIsMissing() {
    var recorder = new RequestRecorder(req -> {
      String path = req.url().getPath();
      if (path.equals("/topology") || path.equals("/health")) {
        return response(HttpStatus.NOT_FOUND, "");
      }
      if (path.equals("/actuator/health")) {
        return response(HttpStatus.OK, "");
      }
      return response(HttpStatus.NOT_FOUND, "");
    });
    var service = new TopologyService(buildClient(recorder));

    StepVerifier.create(service.getTopologyForService("svc", ENDPOINT))
      .assertNext(info -> {
        assertThat(info.getStatus()).isEqualTo(TopologyStatus.HEALTHY);
        assertThat(info.hasMetadata()).isFalse();
      })
      .verifyComplete();

    assertThat(recorder.requestedPaths).containsExactly(
      "/topology",
      "/health",
      "/actuator/health"
    );
  }

  @Test
  void unhealthyWhenTopologyAndAllHealthProbesFail() {
    var recorder = new RequestRecorder(req ->
      response(HttpStatus.INTERNAL_SERVER_ERROR, "")
    );
    var service = new TopologyService(buildClient(recorder));

    StepVerifier.create(service.getTopologyForService("svc", ENDPOINT))
      .assertNext(info -> {
        assertThat(info.getStatus()).isEqualTo(TopologyStatus.UNHEALTHY);
        assertThat(info.hasMetadata()).isFalse();
      })
      .verifyComplete();

    assertThat(recorder.requestedPaths).containsExactly(
      "/topology",
      "/health",
      "/actuator/health"
    );
  }

  @Test
  void joinsPathCorrectlyWhenEndpointHasTrailingSlash() {
    var recorder = new RequestRecorder(req ->
      jsonResponse(HttpStatus.OK, "{\"key\":\"value\"}")
    );
    var service = new TopologyService(buildClient(recorder));

    StepVerifier.create(service.getTopologyForService("svc", ENDPOINT + "/"))
      .assertNext(info ->
        assertThat(info.getStatus()).isEqualTo(TopologyStatus.HEALTHY)
      )
      .verifyComplete();

    assertThat(recorder.requestedPaths).containsExactly("/topology");
  }

  private WebClient buildClient(ExchangeFunction exchange) {
    return WebClient.builder().exchangeFunction(exchange).build();
  }

  private static Mono<ClientResponse> response(HttpStatus status, String body) {
    return Mono.just(
      ClientResponse.create(status).body(body == null ? "" : body).build()
    );
  }

  private static Mono<ClientResponse> jsonResponse(
    HttpStatus status,
    String body
  ) {
    return Mono.just(
      ClientResponse.create(status)
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body(body)
        .build()
    );
  }

  private static class RequestRecorder implements ExchangeFunction {

    final List<String> requestedPaths = new ArrayList<>();
    private final Function<ClientRequest, Mono<ClientResponse>> handler;

    RequestRecorder(Function<ClientRequest, Mono<ClientResponse>> handler) {
      this.handler = handler;
    }

    @Override
    public Mono<ClientResponse> exchange(ClientRequest request) {
      requestedPaths.add(request.url().getPath());
      return handler.apply(request);
    }
  }
}
