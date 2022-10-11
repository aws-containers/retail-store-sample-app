package com.amazon.sample.carts.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazon.sample.carts.configuration.DynamoDBConfiguration;
import com.amazon.sample.carts.configuration.DynamoDBProperties;
import com.amazon.sample.carts.repositories.CartEntity;
import com.amazon.sample.carts.repositories.ItemEntity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                    dynamodbContainer.getContainerIpAddress(),
                    dynamodbContainer.getMappedPort(DYNAMODB_PORT));

            TestPropertyValues.of(endpoint).applyTo(configurableApplicationContext);
        }
    }
}
