package com.mongodb.searcher.application.config

import com.mongodb.kotlin.client.MongoClient
import com.mongodb.kotlin.client.MongoDatabase
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoConfig {

    @Value("\${spring.data.mongodb.uri}")
    lateinit var uri: String

    @Value("\${spring.data.mongodb.database}")
    lateinit var databaseName: String

    @Bean
    fun getMongoClient(): MongoClient {
        return MongoClient.create(uri)
    }

    @Bean
    fun mongoDatabase(mongoClient: MongoClient): MongoDatabase {
        return mongoClient.getDatabase(databaseName)
    }
}