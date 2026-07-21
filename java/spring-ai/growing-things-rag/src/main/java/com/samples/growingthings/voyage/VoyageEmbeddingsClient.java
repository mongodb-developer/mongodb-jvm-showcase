package com.samples.growingthings.voyage;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange(
        url = "/v1/embeddings",
        contentType = MediaType.APPLICATION_JSON_VALUE,
        accept = MediaType.APPLICATION_JSON_VALUE
)
public interface VoyageEmbeddingsClient {
  @PostExchange
  EmbeddingsResponse embed(@RequestBody EmbeddingsRequest body);

  record EmbeddingsRequest(
          List<String> input,
          String model,
          String input_type
  ) {}

  record EmbeddingsResponse(
          List<EmbeddingData> data
  ) {}

  record EmbeddingData(
          float[] embedding,
          int index
  ) {}
}


