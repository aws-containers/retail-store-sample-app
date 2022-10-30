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

package com.amazon.sample.carts.configuration;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverterFactory;
import com.amazon.sample.carts.services.DynamoDBCartService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import com.amazon.sample.carts.services.CartService;

@Configuration
@Profile("dynamodb")
public class DynamoDBConfiguration {

    @Bean
    public AmazonDynamoDB amazonDynamoDB(DynamoDBProperties properties) {
        if (!StringUtils.isEmpty(properties.getEndpoint())) {
            return AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), "us-west-2")
            ).build();
        }

        return AmazonDynamoDBClientBuilder.standard().build();
    }

    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig(DynamoDBProperties properties) {
        // Create empty DynamoDBMapperConfig builder
        DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
        // Inject missing defaults from the deprecated method
        builder.withTypeConverterFactory(DynamoDBTypeConverterFactory.standard());
        builder.withTableNameResolver((aClass, dynamoDBMapperConfig) -> {
            return properties.getTableName();
        });

        return builder.build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB, DynamoDBMapperConfig config) {
        return new DynamoDBMapper(amazonDynamoDB, config);
    }

    @Bean
    public CartService dynamoCartService(DynamoDBMapper mapper, AmazonDynamoDB amazonDynamoDB, DynamoDBProperties properties) {
        return new DynamoDBCartService(mapper, amazonDynamoDB, properties.isCreateTable());
    }
}
