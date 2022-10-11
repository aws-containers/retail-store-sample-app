package com.amazon.sample.carts.configuration;

import com.amazon.sample.carts.services.CartService;
import com.amazon.sample.carts.services.InMemoryCartService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!mongo & !dynamodb")
public class InMemoryConfiguration {
    @Bean
    public CartService cartService() {
        return new InMemoryCartService();
    }
}
