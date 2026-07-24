package com.example.showcase.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.result.InsertManyResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class DataInit implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(DataInit.class);

	private final MongoClient mongoClient;
	private final ArticleVectorIndexProperties properties;

	public DataInit(MongoClient mongoClient, ArticleVectorIndexProperties properties) {
		this.mongoClient = mongoClient;
		this.properties = properties;
	}

	@Override
	public void run(ApplicationArguments args) {

		var collection = mongoClient
				.getDatabase(properties.database())
				.getCollection(properties.collection());

		long existing = collection.countDocuments();
		if (existing > 0) {
			log.info("Skipping dataset ingestion: collection already has {} article(s).", existing);
			return;
		}

		var list = List.of(
				new Document("title", "Kotlin + Quarkus: Working With MongoDB Aggregation Framework")
						.append("content", "A short video about using Kotlin, Quarkus and the MongoDB Aggregation Framework")
						.append("url", "https://www.youtube.com/shorts/-gQ9LXyEACQ"),
				new Document("title", "What Is a Binary Tree?")
						.append("content", "An introduction to the binary tree data structure, its nodes and hierarchical organization")
						.append("url", "https://medium.com/javarevisited/what-is-a-binary-tree-1b389b05fec2"),
				new Document("title", "Clean and Modular Java: A Hexagonal Architecture Approach")
						.append("content", "An approach to organizing Java applications in a modular way using hexagonal architecture concepts")
						.append("url", "https://foojay.io/today/clean-and-modular-java-a-hexagonal-architecture-approach/"),
				new Document("title", "Beyond Basics: Enhancing Kotlin Ktor API With Vector Search")
						.append("content", "Extending a Kotlin Ktor API to add vector search using MongoDB Atlas Vector Search.")
						.append("url", "https://dev.to/ricardohsmello/test-3d5p"),
				new Document("title", "Java Meets Queryable Encryption: Developing a Secure Bank Account Application")
						.append("content", "A Java banking application demonstrating automatic Queryable Encryption with MongoDB to protect sensitive, queryable data.")
						.append("url", "https://dev.to/ricardohsmello/java-meets-queryable-encryption-developing-a-secure-bank-account-application-40im"),
				new Document("title", "Beyond Basics: Enhancing Kotlin Ktor API With Vector Search")
						.append("content", "Extending a Kotlin Ktor API to add vector search using MongoDB Atlas Vector Search.")
						.append("url", "https://dev.to/ricardohsmello/test-3d5p"),
				new Document("title", "Spring Data Unlocked: Advanced Queries With MongoDB")
						.append("content", "Advanced queries with MongoRepository, MongoTemplate, @Query, @Update, @Aggregation, pagination and bulk operations.")
						.append("url", "https://dev.to/ricardohsmello/spring-data-unlocked-advanced-queries-with-mongodb-4jef"),
				new Document("title", "Why Mirroring Production in Dev Helps You Avoid Costly Mistakes")
						.append("content", "A discussion of the differences between MongoDB environments and the risks of testing queries and aggregations on tiers that differ from production.")
						.append("url", "https://foojay.io/today/why-mirroring-production-in-dev-helps-you-avoid-costly-mistakes/"),
				new Document("title", "MongoDB Sharding: What to Know Before You Shard")
						.append("content", "An introduction to the concepts, components and decisions required before distributing a collection with MongoDB Sharding.")
						.append("url", "https://foojay.io/today/mongodb-sharding-what-to-know-before-you-shard/"),
				new Document("title", "Real-Time Fraud Detection in Java with Kafka Streams and Vector Similarity")
						.append("content", "A real-time fraud detection architecture using Java, Kafka Streams, protection rules and vector similarity in MongoDB.")
						.append("url", "https://dev.to/mongodb/real-time-fraud-detection-in-java-with-kafka-streams-and-vector-similarity-n2a"),
				new Document("title", "Deploying a Quarkus Application to AWS Elastic Beanstalk")
						.append("content", "A guide to deploying a Quarkus application on AWS Elastic Beanstalk, with benefits and best practices.")
						.append("url", "https://itnext.io/deploying-a-quarkus-application-to-aws-elastic-beanstalk-73c7a1962a32"),
				new Document("title", "Quarkus + Angular With Keycloak — Part 3")
						.append("content", "The final part of building an application with Quarkus, Angular and authentication via Keycloak.")
						.append("url", "https://itnext.io/quarkus-angular-secured-with-keycloak-pt3-44a766886a66")
		);

		InsertManyResult articles = collection.insertMany(list);

		log.info("Ingested {} article(s).", articles.getInsertedIds().size());
	}
}
