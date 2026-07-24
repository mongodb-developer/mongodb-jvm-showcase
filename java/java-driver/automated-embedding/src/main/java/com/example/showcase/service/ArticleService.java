package com.example.showcase.service;

import com.example.showcase.config.ArticleVectorIndexProperties;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.search.SearchPath.fieldPath;
import static com.mongodb.client.model.search.VectorSearchOptions.approximateVectorSearchOptions;
import static com.mongodb.client.model.search.VectorSearchQuery.textQuery;

@Service
public class ArticleService {

	private final MongoClient mongoClient;
	private final ArticleVectorIndexProperties properties;

	public ArticleService(MongoClient mongoClient, ArticleVectorIndexProperties properties) {
		this.mongoClient = mongoClient;
		this.properties = properties;
	}


	public List<Document> semanticSearch(String query, int limit) {

		var pipeline = List.of(
				Aggregates.vectorSearch(
						fieldPath(properties.path()),
						textQuery(query),
						properties.name(),
						limit,
						approximateVectorSearchOptions(Math.max(limit, properties.numCandidates()))
				),
				Aggregates.project(Projections.fields(
						Projections.excludeId(),
						Projections.include("title", "content", "url"),
						Projections.metaVectorSearchScore("score")
				))
		);

		return mongoClient
				.getDatabase(properties.database())
				.getCollection(properties.collection())
				.aggregate(pipeline)
				.into(new ArrayList<>());
	}
}
