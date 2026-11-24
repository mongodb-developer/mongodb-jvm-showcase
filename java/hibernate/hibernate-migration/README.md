# Hibernate Migration: PostgreSQL to MongoDB

Companion code for the article **"Just Two Files? Migrating a Hibernate ORM Application from PostgreSQL to MongoDB"**.

## Purpose

This folder holds two copies of the same Spring Boot + Hibernate ORM application:

| Project | Database |
|---|---|
| `hibernate-migration-postgresql` | PostgreSQL |
| `hibernate-migration-mongodb` | MongoDB |

The Java code (entities, repository, service, controller, tests) is identical in both projects. Nothing in the core of the application changes.

## What changes

Only two files change the application's behavior:

| File | PostgreSQL | MongoDB |
|---|---|---|
| `pom.xml` | JPA starter + PostgreSQL JDBC driver | `mongodb-hibernate-spring-boot-starter` |
| `src/main/resources/application.yaml` | JDBC datasource (URL, user, password) and `ddl-auto` | MongoDB URI, `database-platform: MongoDB` and null semantics |

The `compose.yaml` also differs, because each project starts its own database container. It is infrastructure only, not application code.

To see it yourself, run:

```bash
diff -r hibernate-migration-postgresql hibernate-migration-mongodb -x target -x .idea
```

## Prerequisites

- Java 21
- Docker and Docker Compose
- The Maven wrapper (`./mvnw`) included in each project

## Configuration

No credentials are required. Both projects connect to a local database:

- PostgreSQL: `jdbc:postgresql://localhost:5432/orders` (user `postgres`, password `postgres`, local development only)
- MongoDB: `mongodb://localhost:27017/orders`

## How to run

Pick one project and run it from its own directory.

PostgreSQL:

```bash
cd hibernate-migration-postgresql
docker compose up -d
./mvnw spring-boot:run
```

MongoDB:

```bash
cd hibernate-migration-mongodb
docker compose up -d
./mvnw spring-boot:run
```

Then send the sample requests in `orders.http`. The same requests work against both projects.

## MongoDB Atlas requirements

None for local use. The MongoDB project runs against the `mongodb/mongodb-atlas-local` container. To use a hosted MongoDB Atlas cluster instead, set `spring.mongodb.uri` to your Atlas connection string through an environment variable and never commit it.
