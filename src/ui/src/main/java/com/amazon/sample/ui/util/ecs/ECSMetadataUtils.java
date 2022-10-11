package com.amazon.sample.ui.util.ecs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ECSMetadataUtils {

    public final static String ECS_METADATA_ENV = "ECS_CONTAINER_METADATA_URI_V4";

    private WebClient client;

    public ECSMetadataUtils() {
        ObjectMapper newMapper = new ObjectMapper();
        newMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        newMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(newMapper)))
                .build();

        this.client = WebClient.builder().exchangeStrategies(exchangeStrategies).baseUrl(getMetadataUri()).build();
    }

    public static final String getMetadataUri() {
        return System.getenv(ECS_METADATA_ENV);
    }

    public Mono<ECSTaskMetadata> getTaskMetadata() {
        return client.get().uri("/task").retrieve().bodyToMono(ECSTaskMetadata.class);
    }
}
