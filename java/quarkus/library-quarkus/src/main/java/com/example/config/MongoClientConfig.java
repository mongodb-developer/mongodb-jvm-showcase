package com.example.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.quarkus.mongodb.runtime.MongoClientCustomizer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class MongoClientConfig implements MongoClientCustomizer {

    @Inject
    MongoSettings settings;

    @ConfigProperty(name = "quarkus.mongodb.database", defaultValue = "library")
    String databaseName;

    @Produces
    @ApplicationScoped
    MongoDatabase libraryDatabase(MongoClient client) {
        return client.getDatabase(databaseName);
    }

    @Override
    public MongoClientSettings.Builder customize(MongoClientSettings.Builder builder) {
        MongoSettings.Pool pool = settings.pool();

        builder
                .applicationName(settings.applicationName())
                .readPreference(ReadPreference.valueOf(settings.readPreference()))
                .applyToConnectionPoolSettings(p -> {
                    p.maxSize(pool.maxSize())
                     .minSize(pool.minSize())
                     .maxConnectionIdleTime(pool.maxConnectionIdleTimeMs(), TimeUnit.MILLISECONDS)
                     .maxConnectionLifeTime(pool.maxConnectionLifeTimeMs(), TimeUnit.MILLISECONDS);
                    if (pool.maxWaitTimeMs() > 0) {
                        p.maxWaitTime(pool.maxWaitTimeMs(), TimeUnit.MILLISECONDS);
                    }
                });

        return builder;
    }

}
