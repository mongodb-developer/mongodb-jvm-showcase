package com.example.showcase.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.SearchIndexModel;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.SearchIndexDefinition.vectorSearch;
import static com.mongodb.client.model.VectorSearchIndexFields.autoEmbedField;

@Component
@Order(1)
public class VectorIndexInit implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(VectorIndexInit.class);
	private static final Duration POLL_INTERVAL = Duration.ofSeconds(2);

	private final MongoClient mongoClient;
	private final ArticleVectorIndexProperties properties;

	public VectorIndexInit(MongoClient mongoClient, ArticleVectorIndexProperties properties) {
		this.mongoClient = mongoClient;
		this.properties = properties;
	}

	@Override
	public void run(ApplicationArguments args) throws InterruptedException {

		MongoDatabase database = mongoClient.getDatabase(properties.database());
		ensureCollectionExists(database);

		MongoCollection<Document> collection = database.getCollection(properties.collection());

		if (findIndex(collection).isEmpty()) {
			createIndex(collection);
		}
		else {
			log.info("Vector search index '{}' already exists on '{}.{}'.",
					properties.name(), properties.database(), properties.collection());
		}

		awaitQueryable(collection);
	}

	private void ensureCollectionExists(MongoDatabase database) {
		boolean exists = StreamSupport.stream(database.listCollectionNames().spliterator(), false)
				.anyMatch(properties.collection()::equals);

		if (!exists) {
			database.createCollection(properties.collection());
			log.info("Created collection '{}.{}'.", properties.database(), properties.collection());
		}
	}

	private void createIndex(MongoCollection<Document> collection) {
		SearchIndexModel indexModel = new SearchIndexModel(
				properties.name(),
				vectorSearch(
						autoEmbedField(properties.path())
								.modality(properties.modality())
								.model(properties.model())
				)
		);

		collection.createSearchIndexes(List.of(indexModel));
		log.info("Requested vector search index '{}' on '{}.{}' (auto-embedding field '{}', model '{}').",
				properties.name(), properties.database(), properties.collection(),
				properties.path(), properties.model());
	}


	private void awaitQueryable(MongoCollection<Document> collection) throws InterruptedException {

		long deadline = System.nanoTime() + Duration.ofSeconds(properties.readyTimeoutSeconds()).toNanos();

		while (System.nanoTime() < deadline) {
			Optional<Document> index = findIndex(collection);

			if (index.filter(it -> it.getBoolean("queryable", false)).isPresent()) {
				log.info("Vector search index '{}' is queryable.", properties.name());
				return;
			}

			String status = index.map(it -> it.getString("status")).orElse("NOT_FOUND");
			if ("FAILED".equals(status)) {
				log.error("Vector search index '{}' failed to build: {}", properties.name(), index.orElseThrow());
				return;
			}

			log.info("Waiting for vector search index '{}' to become queryable (status {}).",
					properties.name(), status);
			Thread.sleep(POLL_INTERVAL.toMillis());
		}

		log.warn("Vector search index '{}' was not queryable within {}s; searches may return no results "
						+ "until the index finishes building.",
				properties.name(), properties.readyTimeoutSeconds());
	}

	private Optional<Document> findIndex(MongoCollection<Document> collection) {
		return collection.listSearchIndexes()
				.into(new ArrayList<>())
				.stream()
				.filter(index -> properties.name().equals(index.getString("name")))
				.findFirst();
	}
}
