package com.devnexus.frauddetection.infrastructure.embedding.voyage;

import java.util.List;

public record EmbeddingsResponse(List<Item> data) {
  public record Item(List<Double> embedding) {}
}
