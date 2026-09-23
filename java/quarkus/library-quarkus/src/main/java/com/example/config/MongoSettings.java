package com.example.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "mongodb")
public interface MongoSettings {

    @WithDefault("devrel-tutorial-java-quarkus-library")
    String applicationName();

    @WithDefault("primary")
    String readPreference();

    Pool pool();

    interface Pool {
        @WithDefault("100")
        int maxSize();

        @WithDefault("0")
        int minSize();

        @WithDefault("0")
        long maxConnectionIdleTimeMs();

        @WithDefault("0")
        long maxConnectionLifeTimeMs();

        @WithDefault("0")
        long maxWaitTimeMs();
    }
}
