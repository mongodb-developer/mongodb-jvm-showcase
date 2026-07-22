package com.mongodb.config;

import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(MongoConfigProperties.class)
public class MongoConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoApplicationNameCustomizer(MongoConfigProperties props) {
        return builder -> {
            if (StringUtils.hasText(props.applicationName())) {
                builder.applicationName(props.applicationName());
            }
        };
    }
}