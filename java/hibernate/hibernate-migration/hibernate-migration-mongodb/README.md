# Hibernate Migration - MongoDB

## Purpose

Same orders application as `../hibernate-migration-postgresql`, migrated to Hibernate ORM with MongoDB using the `mongodb-hibernate` extension.

## Prerequisites

- Java 21
- Maven
- Docker, or a MongoDB Atlas cluster

## Configuration

Set in `src/main/resources/application.yaml`:

- `spring.mongodb.uri`: MongoDB connection string. Do not commit credentials.
- `spring.jpa.database-platform`: `MongoDB`
- `spring.jpa.properties.com.mongodb.hibernate.semantics.nulls`: `MQL` (required)

## How to run

```bash
docker compose up -d
mvn spring-boot:run
```

Use `orders.http` to call the API on `http://localhost:8080`.

## MongoDB Atlas requirements

The extension requires a MongoDB replica set (MongoDB 7.0+). An Atlas cluster works; replace `spring.mongodb.uri` with your Atlas connection string. The included `compose.yaml` starts a local Atlas deployment.
