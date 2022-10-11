package com.amazon.sample.carts.configuration;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("carts.dynamodb")
@Data
public class DynamoDBProperties {
    private String endpoint;

    private boolean createTable;

    private String tableName;
}
