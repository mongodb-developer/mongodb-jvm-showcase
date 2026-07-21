package com.example.langchain4j.config;

import com.example.langchain4j.service.Assistant;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.voyageai.VoyageAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.mongodb.IndexMapping;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class RagConfig {

    @Bean
    public MongoClient mongoClient(@Value("${mongodb.uri}") String uri) {
        return MongoClients.create(
                MongoClientSettings
                        .builder()
                        .applicationName("devrel-tutorial-java-langchain4j")
                        .applyConnectionString(new ConnectionString(uri))
                        .build()
        );
    }

    @Bean
    public EmbeddingModel embeddingModel(
            @Value("${voyage.api-key}") String apiKey,
            @Value("${voyage.model}") String model) {

        return VoyageAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .logRequests(true)   // logs each call sent to Voyage (the embedding requests)
                .logResponses(true)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(
            @Value("${mongodb.uri}") String uri,
            @Value("${mongodb.database}") String database,
            @Value("${mongodb.collection}") String collection,
            @Value("${mongodb.index-name}") String indexName) {

        return MongoDbEmbeddingStore.builder()
                .fromClient(mongoClient(uri))
                .databaseName(database)
                .collectionName(collection)
                .indexName(indexName)
                .createIndex(true)
                .indexMapping(IndexMapping.builder()
                        .dimension(1024)
                        .metadataFieldNames(Set.of())
                        .build())
                .build();
    }

    @Bean
    public ChatModel chatModel(
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model}") String model) {

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .logRequests(true)   // logs the FINAL prompt sent to OpenAI (already with the retrieved context)
                .logResponses(true)
                .build();
    }

    @Bean
    public Assistant assistant(ChatModel chatModel,
                               EmbeddingStore<TextSegment> embeddingStore,
                               EmbeddingModel embeddingModel) {

        var retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .build();

        return AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .build();
    }
}
