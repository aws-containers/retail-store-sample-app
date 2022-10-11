package com.amazon.sample.carts.repositories.mongo;

import com.amazon.sample.carts.repositories.mongo.entities.MongoCartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoCartRepository extends MongoRepository<MongoCartEntity, String> {

}

