package com.amazon.sample.carts.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazon.sample.carts.repositories.CartEntity;
import com.amazon.sample.carts.repositories.ItemEntity;
import com.amazon.sample.carts.repositories.dynamo.entities.DynamoItemEntity;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DynamoDBCartService implements CartService {

    private final DynamoDBMapper mapper;
    private final AmazonDynamoDB dynamoDB;
    private final boolean createTable;

    public DynamoDBCartService(DynamoDBMapper mapper, AmazonDynamoDB dynamoDB, boolean createTable) {
        this.mapper = mapper;
        this.dynamoDB = dynamoDB;
        this.createTable = createTable;
    }

    @PostConstruct
    public void init() {
        if(createTable) {
            DeleteTableRequest deleteTableRequest = mapper.generateDeleteTableRequest(DynamoItemEntity.class);

            try {
                dynamoDB.describeTable(deleteTableRequest.getTableName());

                log.warn("Dynamo table found, deleting to recreate....");
                dynamoDB.deleteTable(deleteTableRequest);
            }
            catch (com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException rnfe) {
                log.warn("Dynamo "+deleteTableRequest.getTableName()+" table not found");
            }

            ProvisionedThroughput pt = new ProvisionedThroughput(1L, 1L);

            CreateTableRequest tableRequest = mapper
                    .generateCreateTableRequest(DynamoItemEntity.class);
            tableRequest.setProvisionedThroughput(pt);
            tableRequest.getGlobalSecondaryIndexes().get(0).setProvisionedThroughput(pt);
            tableRequest.getGlobalSecondaryIndexes().get(0).setProjection(new Projection().withProjectionType("ALL"));
            dynamoDB.createTable(tableRequest);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

        mapper.batchDelete(items);
    }

    @Override
    public CartEntity merge(String sessionId, String customerId) {
        return null;
    }

    @Override
    public ItemEntity add(String customerId, String itemId, int quantity, int unitPrice) {
        String hashKey = hashKey(customerId, itemId);

        DynamoItemEntity item = this.mapper.load(DynamoItemEntity.class, hashKey);

        if(item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        }
        else {
            item = new DynamoItemEntity(hashKey, customerId, itemId, 1, unitPrice);
        }

        this.mapper.save(item);

        return item;
    }

    @Override
    public List<DynamoItemEntity> items(String customerId) {
        final DynamoItemEntity gsiKeyObj = new DynamoItemEntity();
        gsiKeyObj.setCustomerId(customerId);
        final DynamoDBQueryExpression<DynamoItemEntity> queryExpression =
                new DynamoDBQueryExpression<>();
        queryExpression.setHashKeyValues(gsiKeyObj);
        queryExpression.setIndexName("idx_global_customerId");
        queryExpression.setConsistentRead(false);   // cannot use consistent read on GSI
        final PaginatedQueryList<DynamoItemEntity> results =
                mapper.query(DynamoItemEntity.class, queryExpression);

        return new ArrayList<>(results);
    }

    @Override
    public Optional<DynamoItemEntity> item(String customerId, String itemId) {
        return Optional.of(mapper.load(DynamoItemEntity.class, hashKey(customerId, itemId)));
    }

    @Override
    public void deleteItem(String customerId, String itemId) {
        item(customerId, itemId).ifPresentOrElse(this.mapper::delete,
        ()
            -> log.warn("Item missing for delete {} -- {}", customerId, itemId));
    }

    @Override
    public Optional<DynamoItemEntity> update(String customerId, String itemId, int quantity, int unitPrice) {
        return item(customerId, itemId).map(
            item -> {
                item.setQuantity(quantity);
                item.setUnitPrice(unitPrice);

                this.mapper.save(item);

                return item;
            }
        );
    }

    @Override
    public boolean exists(String customerId) {
        return this.items(customerId).size() > 0;
    }

    private String hashKey(String customerId, String itemId) {
        return customerId+":"+itemId;
    }
}
