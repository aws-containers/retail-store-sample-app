package com.amazon.sample.carts.services;

import com.amazon.sample.carts.repositories.CartEntity;
import com.amazon.sample.carts.repositories.ItemEntity;
import com.amazon.sample.carts.repositories.mongo.MongoCartRepository;
import com.amazon.sample.carts.repositories.mongo.MongoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@Testcontainers
@ContextConfiguration(initializers = MongoCartServiceTests.Initializer.class)
@ActiveProfiles({"mongo"})
@Tag("docker")
public class MongoCartServiceTests extends AbstractServiceTests {

    private static final int MONGO_PORT = 27017;

    @Autowired
    private MongoCartRepository cartRepository;

    @Autowired
    private MongoItemRepository itemRepository;

    private MongoCartService service;

    @Container
    public static GenericContainer container =
            new GenericContainer<>("mongo:4.2.11")
                    .withExposedPorts(MONGO_PORT);

    @BeforeEach
    void setUp() {
        this.service = new MongoCartService(cartRepository, itemRepository);
    }

    @Override
    public CartService getService() {
        return this.service;
    }

    public static class Initializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String endpoint = String.format("spring.data.mongodb.uri=mongodb://%s:%s/data",
                    container.getContainerIpAddress(),
                    container.getMappedPort(MONGO_PORT));

            TestPropertyValues.of(endpoint).applyTo(configurableApplicationContext);
        }
    }
}
