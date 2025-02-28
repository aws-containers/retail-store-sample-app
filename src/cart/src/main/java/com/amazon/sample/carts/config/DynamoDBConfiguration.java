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

package com.amazon.sample.carts.config;

import com.amazon.sample.carts.services.CartService;
import com.amazon.sample.carts.services.DynamoDBCartService;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@Configuration
@ConditionalOnProperty(
  prefix = "retail.cart.persistence",
  name = "provider",
  havingValue = "dynamodb"
)
@Slf4j
public class DynamoDBConfiguration {

  @Bean
  DynamoDbClient dynamoDbClient(DynamoDBProperties properties) {
    log.info("Using DynamoDB persistence");
    log.info("DynamoDB table: {}", properties.getTableName());

    DynamoDbClientBuilder builder = DynamoDbClient.builder();

    if (StringUtils.hasLength(properties.getEndpoint())) {
      log.info("DynamoDB endpoint: {}", properties.getEndpoint());

      builder.region(Region.US_WEST_2);
      builder.endpointOverride(URI.create(properties.getEndpoint()));
    }

    return builder.build();
  }

  @Bean
  public DynamoDbEnhancedClient dynamoDbEnhancedClient(
    DynamoDbClient dynamoDbClient
  ) {
    return DynamoDbEnhancedClient.builder()
      .dynamoDbClient(dynamoDbClient)
      .build();
  }

  @Bean
  public CartService dynamoCartService(
    DynamoDbClient dynamoDbClient,
    DynamoDbEnhancedClient dynamoDbEnhancedClient,
    DynamoDBProperties properties
  ) {
    return new DynamoDBCartService(
      dynamoDbClient,
      dynamoDbEnhancedClient,
      properties.isCreateTable(),
      properties.getTableName()
    );
  }
}
