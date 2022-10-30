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

package com.amazon.sample.carts.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazon.sample.carts.configuration.DynamoDBConfiguration;
import com.amazon.sample.carts.configuration.DynamoDBProperties;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    classes = {DynamoDBCartServiceTests.TestConfiguration.class, DynamoDBProperties.class},
    properties = {
            "carts.dynamodb.createTable=true"
    })
@Testcontainers
@ContextConfiguration(initializers = DynamoDBCartServiceTests.Initializer.class)
@ActiveProfiles({"dynamodb"})
@Tag("docker")
public class DynamoDBCartServiceTests extends AbstractServiceTests {

    private static final int DYNAMODB_PORT = 8000;

    @Autowired
    private DynamoDBCartService service;

    @Autowired
    private AmazonDynamoDB dynamodb;

    @Container
    public static GenericContainer dynamodbContainer =
            new GenericContainer<>("amazon/dynamodb-local:1.13.3")
                    .withExposedPorts(DYNAMODB_PORT);

    @Override
    public CartService getService() {
        return this.service;
    }

    @EnableAutoConfiguration
    @Configuration
    @Import(DynamoDBConfiguration.class)
    static class TestConfiguration {

        @Autowired
        private AmazonDynamoDB amazonDynamoDB;

        @Autowired
        private DynamoDBMapper mapper;
    }

    public static class Initializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String endpoint = String.format("carts.dynamodb.endpoint=http://%s:%s",
                    dynamodbContainer.getHost(),
                    dynamodbContainer.getMappedPort(DYNAMODB_PORT));

            TestPropertyValues.of(endpoint).applyTo(configurableApplicationContext);
        }
    }
}
