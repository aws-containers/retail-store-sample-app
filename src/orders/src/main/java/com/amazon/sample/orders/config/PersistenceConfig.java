package com.amazon.sample.orders.config;


import com.amazon.sample.orders.entities.OrderEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;

import java.util.UUID;

@Configuration
public class PersistenceConfig {
    @Bean
    BeforeConvertCallback<OrderEntity> beforeSaveCallback() {
        return (entity) -> {
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID().toString());
            }
            return entity;
        };
    }
}
