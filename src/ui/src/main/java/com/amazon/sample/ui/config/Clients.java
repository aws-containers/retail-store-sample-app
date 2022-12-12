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

package com.amazon.sample.ui.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazon.sample.ui.clients.carts.api.CartsApi;
import com.amazon.sample.ui.clients.carts.api.ItemsApi;
import com.amazon.sample.ui.clients.catalog.RFC3339DateFormat;
import com.amazon.sample.ui.clients.catalog.api.CatalogApi;
import com.amazon.sample.ui.clients.checkout.api.CheckoutApi;
import com.amazon.sample.ui.clients.orders.api.OrdersApi;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.text.DateFormat;
import java.util.TimeZone;

@Configuration
@Slf4j
public class Clients {

    @Value("${endpoints.catalog}")
    private String catalogEndpoint;

    @Value("${endpoints.carts}")
    private String cartsEndpoint;

    @Value("${endpoints.orders}")
    private String ordersEndpoint;

    @Value("${endpoints.checkout}")
    private String checkoutEndpoint;

    @Value("${endpoints.logging}")
    private boolean logging;

    @Value("${endpoints.http.keep-alive}")
    private boolean keepAlive;

    private WebClient createWebClient(ObjectMapper mapper, WebClient.Builder webClientBuilder) {
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000) // Connection Timeout
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(10)) // Read Timeout
                                .addHandlerLast(new WriteTimeoutHandler(10))); // Write Timeout

        ExchangeStrategies strategies = ExchangeStrategies
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON));
                }).build();

        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient).keepAlive(keepAlive)))
                .exchangeStrategies(strategies)
                .filters( exchangeFilterFunctions -> {
                    if(logging) {
                        exchangeFilterFunctions.add(logRequest());
                        exchangeFilterFunctions.add(logResponse());
                    }
                })
                .build();
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response: {} {}", clientResponse.rawStatusCode(), clientResponse.bodyToMono(String.class));
            return Mono.just(clientResponse);
        });
    }

    private DateFormat createDefaultDateFormat() {
        DateFormat dateFormat = new RFC3339DateFormat();
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    @Bean
    public ObjectMapper apiClientsObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CatalogApi catalogApi(WebClient.Builder webClientBuilder) {
        ObjectMapper mapper = apiClientsObjectMapper();

        return new CatalogApi(new com.amazon.sample.ui.clients.catalog.ApiClient(this.createWebClient(mapper, webClientBuilder), mapper, createDefaultDateFormat())
                .setBasePath(this.catalogEndpoint));
    }

    @Bean
    public CartsApi cartsApi(WebClient.Builder webClientBuilder) {
        ObjectMapper mapper = apiClientsObjectMapper();

        return new CartsApi(new com.amazon.sample.ui.clients.carts.ApiClient(this.createWebClient(mapper, webClientBuilder), mapper, createDefaultDateFormat())
                .setBasePath(this.cartsEndpoint));
    }

    @Bean
    public ItemsApi itemsApi(WebClient.Builder webClientBuilder) {
        ObjectMapper mapper = apiClientsObjectMapper();

        return new ItemsApi(new com.amazon.sample.ui.clients.carts.ApiClient(this.createWebClient(mapper, webClientBuilder), mapper, createDefaultDateFormat())
                .setBasePath(this.cartsEndpoint));
    }

    @Bean
    public OrdersApi ordersApi(WebClient.Builder webClientBuilder) {
        ObjectMapper mapper = apiClientsObjectMapper();

        return new OrdersApi(new com.amazon.sample.ui.clients.orders.ApiClient(this.createWebClient(mapper, webClientBuilder), mapper, createDefaultDateFormat())
                .setBasePath(this.ordersEndpoint));
    }

    @Bean
    public CheckoutApi checkoutApi(WebClient.Builder webClientBuilder) {
        ObjectMapper mapper = apiClientsObjectMapper();

        return new CheckoutApi(new com.amazon.sample.ui.clients.checkout.ApiClient(this.createWebClient(mapper, webClientBuilder), mapper, createDefaultDateFormat())
                .setBasePath(this.checkoutEndpoint));
    }
}
