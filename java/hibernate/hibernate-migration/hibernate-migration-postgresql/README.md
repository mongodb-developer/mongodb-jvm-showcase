# Hibernate Migration - PostgreSQL

## Purpose

Spring Boot orders application using Hibernate ORM with PostgreSQL. It is the starting point of the migration to MongoDB; see `../hibernate-migration-mongodb` for the migrated version.

## Prerequisites

- Java 21
- Maven
- Docker

## Configuration

Defaults are in `src/main/resources/application.yaml`:

- `spring.datasource.url`: `jdbc:postgresql://localhost:5432/orders`
- `spring.datasource.username` / `spring.datasource.password`: `postgres`

## How to run

```bash
docker compose up -d
mvn spring-boot:run
```

Use `orders.http` to call the API on `http://localhost:8080`.

## MongoDB Atlas requirements

Not applicable.
