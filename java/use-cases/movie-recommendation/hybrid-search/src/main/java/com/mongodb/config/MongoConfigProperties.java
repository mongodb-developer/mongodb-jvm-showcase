package com.mongodb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongodb")
public record MongoConfigProperties(
    String applicationName)
{}