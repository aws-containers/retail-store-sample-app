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

import com.amazon.sample.ui.config.EndpointProperties;
import com.amazon.sample.ui.web.util.TopologyInformation;
import com.amazon.sample.ui.web.util.TopologyStatus;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Controller
@RequestMapping("/topology")
@Slf4j
public class TopologyController {

  private static final int CONNECT_TIMEOUT = 1000;
  private static final int RESPONSE_TIMEOUT = 1000;

  @Autowired
  private EndpointProperties endpoints;

  @GetMapping
  public String topology(Model model) {
    HttpClient httpClient = HttpClient.create()
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT)
      .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT));

    var webClient = WebClient.builder()
      .clientConnector(new ReactorClientHttpConnector(httpClient))
      .build();

    var topologyMap = Flux.merge(
      getTopologyForService(webClient, "catalog", endpoints.getCatalog()),
      getTopologyForService(webClient, "carts", endpoints.getCarts()),
      getTopologyForService(webClient, "checkout", endpoints.getCheckout()),
      getTopologyForService(webClient, "orders", endpoints.getOrders())
    ).collectMap(TopologyInformation::getServiceName, topology -> topology);

    model.addAttribute("topology", topologyMap);

    return "topology";
  }

  private Mono<TopologyInformation> getTopologyForService(
    WebClient webClient,
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

    topology.setStatus(TopologyStatus.HEALTHY);

    String topologyUrl = endpoint.endsWith("/")
      ? endpoint + "topology"
      : endpoint + "/topology";

    return webClient
      .get()
      .uri(topologyUrl)
      .retrieve()
      .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
      .map(metadata -> {
        topology.setMetadata(metadata);
        return topology;
      })
      .onErrorResume(e -> {
        log.error(
          "Error fetching topology for service {}: {}",
          serviceName,
          e.getMessage()
        );
        topology.setStatus(TopologyStatus.UNHEALTHY);
        return Mono.just(topology);
      });
  }
}
