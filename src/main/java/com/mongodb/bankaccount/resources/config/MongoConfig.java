package com.mongodb.bankaccount.resources.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class MongoConfig {

    private final EncryptionConfig encryptionConfig;

    public MongoConfig(EncryptionConfig encryptionConfig) {
        this.encryptionConfig = encryptionConfig;
    }

    @Bean
    public MongoClient mongoClient() throws Exception {
        MongoClientSettings mongoClientSettings = encryptionConfig.getMongoClientSettings();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        return mongoClient.getDatabase(encryptionConfig.getEncryptedDatabaseName()).withCodecRegistry(pojoCodecRegistry);
    }
}