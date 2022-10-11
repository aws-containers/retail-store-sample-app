package com.amazon.sample.ui.chaos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Endpoint(id="fail-cart")
public class FailCartActuatorEndpoint {

    private WebClient client;

    @Autowired
    public FailCartActuatorEndpoint(WebClient.Builder webClientBuilder, @Value("${endpoints.carts}") String cartsEndpoint) {
        client = WebClient.create(cartsEndpoint);
    }

    @WriteOperation
    public Mono<String> activate() {
        return this.client.post().uri("/actuator/fail").retrieve().bodyToMono(String.class);
    }
}
