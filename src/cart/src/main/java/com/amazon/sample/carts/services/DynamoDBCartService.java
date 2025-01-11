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

import com.amazon.sample.carts.repositories.CartEntity;
import com.amazon.sample.carts.repositories.ItemEntity;
import com.amazon.sample.carts.repositories.dynamo.entities.DynamoItemEntity;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Slf4j
public class DynamoDBCartService
  implements CartService, ApplicationListener<ApplicationReadyEvent> {

  private final DynamoDbClient dynamoDBClient;
  private final boolean createTable;
  private final String tableName;
  private final DynamoDbTable<DynamoItemEntity> table;

  static final TableSchema<DynamoItemEntity> CART_TABLE_SCHEMA =
    TableSchema.fromClass(DynamoItemEntity.class);

  public DynamoDBCartService(
    DynamoDbClient dynamoDBClient,
    DynamoDbEnhancedClient dynamoDbEnhancedClient,
    boolean createTable,
    String tableName
  ) {
    this.dynamoDBClient = dynamoDBClient;
    this.createTable = createTable;
    this.tableName = tableName;

    this.table = dynamoDbEnhancedClient.table(tableName, CART_TABLE_SCHEMA);
  }

  @Override
  public void onApplicationEvent(final @NonNull ApplicationReadyEvent event) {
    this.items("test");
  }

  @PostConstruct
  public void init() {
    if (createTable) {
      try {
        dynamoDBClient.deleteTable(
          DeleteTableRequest.builder().tableName(this.tableName).build()
        );
      } catch (ResourceNotFoundException rnfe) {
        log.warn("Dynamo table not found");
      }

      this.table.createTable(builder ->
          builder
            .globalSecondaryIndices(builder3 ->
              builder3
                .indexName("idx_global_customerId")
                .projection(builder2 ->
                  builder2.projectionType(ProjectionType.ALL)
                )
                .provisionedThroughput(builder4 ->
                  builder4.writeCapacityUnits(1L).readCapacityUnits(1L)
                )
            )
            .provisionedThroughput(b ->
              b.readCapacityUnits(1L).writeCapacityUnits(1L).build()
            )
        );

      this.dynamoDBClient.waiter()
        .waitUntilTableExists(b -> b.tableName(this.tableName));
    }
  }

  @Override
  public CartEntity get(String customerId) {
    List<DynamoItemEntity> items = items(customerId);

    return new CartEntity() {
      @Override
      public String getCustomerId() {
        return customerId;
      }

      @Override
      public List<? extends ItemEntity> getItems() {
        return items;
      }
    };
  }

  @Override
  public void delete(String customerId) {
    List<DynamoItemEntity> items = items(customerId);

    items.forEach(item -> {
      this.table.deleteItem(item);
    });
  }

  @Override
  public CartEntity merge(String sessionId, String customerId) {
    return null;
  }

  @Override
  public ItemEntity add(
    String customerId,
    String itemId,
    int quantity,
    int unitPrice
  ) {
    String hashKey = hashKey(customerId, itemId);

    DynamoItemEntity item =
      this.table.getItem(
          Key.builder().partitionValue(hashKey(customerId, itemId)).build()
        );

    if (item != null) {
      item.setQuantity(item.getQuantity() + quantity);
    } else {
      item = new DynamoItemEntity(hashKey, customerId, itemId, 1, unitPrice);
    }

    this.table.putItem(item);

    return item;
  }

  @Override
  public List<DynamoItemEntity> items(String customerId) {
    DynamoDbIndex<DynamoItemEntity> index =
      this.table.index("idx_global_customerId");
    QueryConditional q = QueryConditional.keyEqualTo(
      Key.builder().partitionValue(customerId).build()
    );
    Iterator<Page<DynamoItemEntity>> result = index.query(q).iterator();
    List<DynamoItemEntity> users = new ArrayList<>();

    while (result.hasNext()) {
      Page<DynamoItemEntity> userPage = result.next();
      users.addAll(userPage.items());
    }

    return users;
  }

  @Override
  public Optional<DynamoItemEntity> item(String customerId, String itemId) {
    return Optional.of(
      this.table.getItem(
          Key.builder().partitionValue(hashKey(customerId, itemId)).build()
        )
    );
  }

  @Override
  public void deleteItem(String customerId, String itemId) {
    item(customerId, itemId).ifPresentOrElse(this.table::deleteItem, () ->
      log.warn("Item missing for delete {} -- {}", customerId, itemId)
    );
  }

  @Override
  public Optional<DynamoItemEntity> update(
    String customerId,
    String itemId,
    int quantity,
    int unitPrice
  ) {
    return item(customerId, itemId).map(item -> {
      item.setQuantity(quantity);
      item.setUnitPrice(unitPrice);

      this.table.updateItem(item);

      return item;
    });
  }

  @Override
  public boolean exists(String customerId) {
    return this.items(customerId).size() > 0;
  }

  private String hashKey(String customerId, String itemId) {
    return customerId + ":" + itemId;
  }
}
