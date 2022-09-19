package com.sysco.cdp.configurations;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfiguration {

    @Bean
    public AmazonDynamoDB getDynamoDBClient() {
        return AmazonDynamoDBClientBuilder.defaultClient();
    }

}
