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

import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
@Slf4j
public class TopologyService {

  private static final int CONNECT_TIMEOUT = 1000;
  private static final int RESPONSE_TIMEOUT = 1000;

  private final WebClient webClient;

  @Autowired
  public TopologyService(WebClient.Builder webClientBuilder) {
    HttpClient httpClient = HttpClient.create()
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT)
      .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT));

    this.webClient = webClientBuilder
      .clientConnector(new ReactorClientHttpConnector(httpClient))
      .build();
  }

  TopologyService(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<TopologyInformation> getTopologyForService(
    String serviceName,
    String endpoint
  ) {
    var topology = new TopologyInformation();
    topology.setServiceName(serviceName);
    topology.setEndpoint(endpoint);
    topology.setStatus(TopologyStatus.NONE);

    if (endpoint == null || endpoint.isEmpty()) {
      return Mono.just(topology);
    }

    return fetchTopology(endpoint)
      .map(metadata -> {
        topology.setStatus(TopologyStatus.HEALTHY);
        topology.setMetadata(metadata);
        return topology;
      })
      .onErrorResume(topologyError -> {
        log.debug(
          "Topology endpoint unavailable for service {}: {} — falling back to health check",
          serviceName,
          topologyError.getMessage()
        );
        return checkHealth(endpoint).map(healthy -> {
          topology.setStatus(
            healthy ? TopologyStatus.HEALTHY : TopologyStatus.UNHEALTHY
          );
          if (!healthy) {
            log.warn(
              "Health check failed for service {} at {}",
              serviceName,
              endpoint
            );
          }
          return topology;
        });
      });
  }

  private Mono<Map<String, String>> fetchTopology(String endpoint) {
    return webClient
      .get()
      .uri(joinPath(endpoint, "topology"))
      .retrieve()
      .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {});
  }

  private Mono<Boolean> checkHealth(String endpoint) {
    return probeHealth(joinPath(endpoint, "health")).flatMap(ok ->
      ok ? Mono.just(true) : probeHealth(joinPath(endpoint, "actuator/health"))
    );
  }

  private Mono<Boolean> probeHealth(String url) {
    return webClient
      .get()
      .uri(url)
      .retrieve()
      .toBodilessEntity()
      .map(response -> response.getStatusCode().is2xxSuccessful())
      .onErrorReturn(false);
  }

  private String joinPath(String endpoint, String path) {
    return endpoint.endsWith("/") ? endpoint + path : endpoint + "/" + path;
  }
}
