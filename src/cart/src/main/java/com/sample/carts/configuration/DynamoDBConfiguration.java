package com.amazon.sample.carts.configuration;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverterFactory;
import com.amazon.sample.carts.services.DynamoDBCartService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import com.amazon.sample.carts.services.CartService;

@Configuration
@Profile("dynamodb")
public class DynamoDBConfiguration {

    @Bean
    public AmazonDynamoDB amazonDynamoDB(DynamoDBProperties properties) {
        if (!StringUtils.isEmpty(properties.getEndpoint())) {
            return AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), "us-west-2")
            ).build();
        }

        return AmazonDynamoDBClientBuilder.standard().build();
    }

    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig(DynamoDBProperties properties) {
        // Create empty DynamoDBMapperConfig builder
        DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
        // Inject missing defaults from the deprecated method
        builder.withTypeConverterFactory(DynamoDBTypeConverterFactory.standard());
        builder.withTableNameResolver((aClass, dynamoDBMapperConfig) -> {
            return properties.getTableName();
        });

        return builder.build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB, DynamoDBMapperConfig config) {
        return new DynamoDBMapper(amazonDynamoDB, config);
    }

    @Bean
    public CartService dynamoCartService(DynamoDBMapper mapper, AmazonDynamoDB amazonDynamoDB, DynamoDBProperties properties) {
        return new DynamoDBCartService(mapper, amazonDynamoDB, properties.isCreateTable());
    }
}
