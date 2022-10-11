package com.amazon.sample.carts.repositories.mongo;

import com.amazon.sample.carts.repositories.mongo.entities.MongoItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoItemRepository extends MongoRepository<MongoItemEntity, String> {
}

