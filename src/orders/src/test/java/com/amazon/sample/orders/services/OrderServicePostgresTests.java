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

package com.amazon.sample.orders.services;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

import com.amazon.sample.orders.entities.OrderEntity;
import com.amazon.sample.orders.entities.OrderItemEntity;
import com.amazon.sample.orders.entities.ShippingAddressEntity;
import com.amazon.sample.orders.repositories.OrderRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integration")
public class OrderServicePostgresTests {

  @LocalServerPort
  private Integer port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:17.2"
  );

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("retail.orders.persistence.provider", () -> "postgres");
    registry.add(
      "retail.orders.persistence.endpoint",
      () -> postgres.getHost() + ":" + postgres.getMappedPort(5432)
    );
    registry.add("retail.orders.persistence.username", postgres::getUsername);
    registry.add("retail.orders.persistence.password", postgres::getPassword);
    registry.add("retail.orders.persistence.name", postgres::getDatabaseName);
  }

  @Autowired
  OrderRepository orderRepository;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + port;
    orderRepository.deleteAll();
  }

  @Test
  void shouldGetEmptyOrders() {
    given()
      .contentType(ContentType.JSON)
      .when()
      .get("/orders")
      .then()
      .statusCode(200)
      .body(".", hasSize(0));
  }

  @Test
  void shouldGetAllOrders() {
    var items = List.of(new OrderItemEntity("123", 1, 10, 10));

    List<OrderEntity> orders = List.of(
      new OrderEntity(
        items,
        new ShippingAddressEntity(
          "John",
          "Doe",
          "email@example.com",
          "Some Address",
          "",
          "Some City",
          "11111",
          "CA"
        )
      ),
      new OrderEntity(
        items,
        new ShippingAddressEntity(
          "John",
          "Doe",
          "email@example.com",
          "Some Address",
          "",
          "Some City",
          "11111",
          "CA"
        )
      )
    );
    orderRepository.saveAll(orders);

    given()
      .contentType(ContentType.JSON)
      .when()
      .get("/orders")
      .then()
      .statusCode(200)
      .body(".", hasSize(2));
  }
}
