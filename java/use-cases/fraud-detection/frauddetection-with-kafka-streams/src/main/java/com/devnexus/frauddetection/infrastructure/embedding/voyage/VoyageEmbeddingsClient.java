package com.devnexus.frauddetection.infrastructure.embedding.voyage;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        url = "/v1/embeddings",
        contentType = MediaType.APPLICATION_JSON_VALUE,
        accept = MediaType.APPLICATION_JSON_VALUE
)
public interface VoyageEmbeddingsClient {
  @PostExchange
  EmbeddingsResponse embed(@RequestBody EmbeddingsRequest body);
}