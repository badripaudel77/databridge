package com.example.datasyncapp.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class MongoConfig {

    @Value("${com.example.datasyncapp.mongodb.name}")
    private String mongoDBName;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Bean
    @DependsOn("mongoClient")
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(mongoDBName);
    }
}
