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
