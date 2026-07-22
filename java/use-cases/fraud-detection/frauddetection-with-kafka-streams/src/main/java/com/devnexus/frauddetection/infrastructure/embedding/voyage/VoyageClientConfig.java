package com.devnexus.frauddetection.infrastructure.embedding.voyage;

import com.devnexus.frauddetection.infrastructure.embedding.config.VoyageEmbeddingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class VoyageClientConfig {

    @Bean
    public VoyageEmbeddingsClient voyageEmbeddingsClient(VoyageEmbeddingProperties props) {
        RestClient client = RestClient.builder()
            .baseUrl(props.baseUrl())
            .defaultHeader("Authorization", "Bearer " + props.apiKey())
            .defaultHeader("Content-Type", "application/json")
            .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
        return factory.createClient(VoyageEmbeddingsClient.class);
    }
}
