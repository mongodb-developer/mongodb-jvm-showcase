package com.example.langchain4j.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngestionService {

    private final DocumentLoader documentLoader;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final MongoCollection<Document> collection;

    public IngestionService(DocumentLoader documentLoader,
                            EmbeddingModel embeddingModel,
                            EmbeddingStore<TextSegment> embeddingStore,
                            MongoClient mongoClient,
                            @Value("${mongodb.database}") String database,
                            @Value("${mongodb.collection}") String collectionName) {
        this.documentLoader = documentLoader;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.collection = mongoClient.getDatabase(database).getCollection(collectionName);
    }

    public int ingest() throws Exception {

        if (collection.countDocuments() > 0) {
            return 0;
        }

        List<TextSegment> documents = documentLoader.load();

        for (TextSegment document : documents) {
            var embedding = embeddingModel.embed(document).content();
            embeddingStore.add(embedding, document);
        }

        return documents.size();
    }
}
