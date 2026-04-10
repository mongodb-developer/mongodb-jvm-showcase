# kotlin-quarkus-lab

A REST API lab project built with Kotlin and Quarkus, using MongoDB as the database. The application exposes endpoints to query movies from the `sample_mflix` dataset.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.3 |
| Framework | Quarkus 3.34 |
| Database | MongoDB (via Panache for Kotlin) |
| Build Tool | Maven |
| Java Version | 21 |

## Prerequisites

- Java 21+
- Maven (or use the included `./mvnw` wrapper)
- MongoDB running on `localhost:28000` with the `sample_mflix` database

### Spinning up MongoDB

This project uses [mongodb-cli-lab](https://mongodb-cli-lab.vercel.app/) — a community CLI that makes it easy to spin up MongoDB instances locally. It requires Docker to be installed and running.

Install and run:

```shell
npm install -g @ricardohsmello/mongodb-cli-lab
```

```shell
mongodb-cli-lab up --topology standalone --mongodb-version 8.2 --port 28000 --sample-databases sample_mflix
```

## Running in dev mode

```shell
./mvnw quarkus:dev
```

The app will be available at `http://localhost:8080`.

> Dev UI is available at `http://localhost:8080/q/dev/`

## API Endpoints

### List movies

```
GET /movies?page=0&size=20
```

Returns a paginated list of movies.

| Query Param | Default | Description |
|---|---|---|
| `page` | `0` | Page number (zero-based) |
| `size` | `20` | Number of results per page |

### Top frequent actors

```
GET /movies/top-frequent-actors
```

Returns the top 3 actors who appear in the most movies.

## Packaging

```shell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```
