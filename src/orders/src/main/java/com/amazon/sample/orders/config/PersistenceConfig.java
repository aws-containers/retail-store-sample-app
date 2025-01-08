package com.amazon.sample.orders.config;

import com.amazon.sample.orders.entities.OrderEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.RelationalManagedTypes;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;

@Configuration
public class PersistenceConfig extends AbstractJdbcConfiguration {

  @Bean
  BeforeConvertCallback<OrderEntity> beforeSaveCallbackOrder() {
    return entity -> {
      if (entity.getId() == null) {
        String id = UUID.randomUUID().toString();
        entity.setId(id);
        entity.setCreatedDate(LocalDateTime.now());
      }
      return entity;
    };
  }

  @Override
  public JdbcMappingContext jdbcMappingContext(
    Optional<NamingStrategy> namingStrategy,
    JdbcCustomConversions customConversions,
    RelationalManagedTypes jdbcManagedTypes
  ) {
    JdbcMappingContext context = super.jdbcMappingContext(
      namingStrategy,
      customConversions,
      jdbcManagedTypes
    );
    context.setForceQuote(false);
    return context;
  }
}
