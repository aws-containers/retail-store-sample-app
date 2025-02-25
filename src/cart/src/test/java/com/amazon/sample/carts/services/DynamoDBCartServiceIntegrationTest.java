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

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(
  properties = {
    "retail.cart.persistence.provider=dynamodb",
    "retail.cart.persistence.dynamodb.createTable=true",
  }
)
@Testcontainers
@Tag("integration")
public class DynamoDBCartServiceIntegrationTest extends AbstractServiceTests {

  @Container
  static DynamoDBContainer dynamodbContainer = new DynamoDBContainer(
    DockerImageName.parse("amazon/dynamodb-local:1.20.0")
  );

  @DynamicPropertySource
  static void dynamoDbProperties(DynamicPropertyRegistry registry) {
    registry.add("retail.cart.persistence.dynamodb.endpoint", () ->
      String.format(
        "http://%s:%d",
        dynamodbContainer.getHost(),
        dynamodbContainer.getMappedPort(8000)
      )
    );
  }

  @Autowired
  private DynamoDBCartService service;

  @Override
  public CartService getService() {
    return this.service;
  }
}

class DynamoDBContainer extends GenericContainer<DynamoDBContainer> {

  private static final int DYNAMODB_PORT = 8000;

  public DynamoDBContainer(DockerImageName image) {
    super(image);
    withExposedPorts(DYNAMODB_PORT);
  }
}
