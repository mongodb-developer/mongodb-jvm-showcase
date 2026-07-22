# spring-data-mongodb-hybrid-search

This project is part of the “Beyond Keywords” article series:

- [Beyond Keywords: Implementing Semantic Search in Java With Spring Data (Part 1)](https://dev.to/mongodb/beyond-keywords-implementing-semantic-search-in-java-with-spring-data-part-1-3m68)
- [Beyond Keywords: Optimizing Vector Search With Filters and Caching (Part 2)](https://dev.to/mongodb/beyond-keywords-optimizing-vector-search-with-filters-and-caching-part-2-4e50)
- [Beyond Keywords: Hybrid Search With Atlas and Vector Search (Part 3)](https://dev.to/mongodb/beyond-keywords-hybrid-search-with-atlas-and-vector-search-part-3-5fp3)

where we explore how to go beyond simple keyword search and build smarter applications with MongoDB and Spring.

It started with semantic search using vector search and Voyage AI embeddings, then evolved to include pre-filters for more precise results, caching strategies to save on embedding generation, and finally Hybrid Search — combining Atlas Search (full-text) with vector search through $rankFusion.

The project demonstrates:

- Vector Search with pre-filters (e.g., genres, year, IMDb rating).
- Atlas Search with compound queries, filters, and should clauses.
- Caching strategies to avoid unnecessary embedding calls.
- Hybrid Search that merges vector similarity and keyword matching.

It also includes a minimal Bootstrap-based UI served by Spring (/static/index.html) and a REST endpoint.

<img src="docs/img/webApp.png" alt="The Movie Search web application" />

## Built With

- Java 21
- Spring Boot Starter Data MongoDB 3.5.4
- [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register) 
- [Voyage AI Embeddings API](https://www.voyageai.com/)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/mongodb-developer/spring-data-mongodb-hybrid-search.git
cd spring-data-mongodb-hybrid-search
```

### 2. Get a MongoDB cluster

You need a MongoDB cluster with Search enabled and the `sample_mflix` sample dataset (the app uses the `embedded_movies` collection). Pick **one** of the options below.

#### Option A — Atlas (cloud)

1. Create a free cluster on [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register).
2. Load the sample dataset (`... > Load Sample Dataset`) so `sample_mflix.embedded_movies` is available.
3. Copy your connection string (`Connect > Drivers`).

#### Option B — Run local instead (optional)

Use [`mongodb-cli-lab`](https://www.npmjs.com/package/@ricardohsmello/mongodb-cli-lab) — it spins up a local replica set with Search enabled, so you don't need an Atlas cluster.

```bash
npm install -g @ricardohsmello/mongodb-cli-lab
mongodb-cli-lab quickstart --topology replica-set --replicas 3 --search --mongodb-version 8.2 --port 28000
```

Your local connection string will be something like `mongodb://localhost:28000/?directConnection=true`.

### 3. Create the Search indexes

On the `sample_mflix.embedded_movies` collection, create the two indexes below (Atlas UI: `Atlas Search > Create Search Index`, or via `mongosh`).

**Full-text search index** (Atlas Search):

```json
{
  "mappings": {
    "dynamic": true
  }
}
```

**Vector search index** (Atlas Vector Search):

```json
{
  "fields": [
    {
      "type": "vector",
      "path": "plot_embedding_voyage_3_large",
      "numDimensions": 2048,
      "similarity": "dotProduct"
    },
    {
      "type": "filter",
      "path": "imdb.rating"
    },
    {
      "type": "filter",
      "path": "year"
    },
    {
      "type": "filter",
      "path": "genres"
    }
  ]
}
```

The index names must match the ones in `application.yml` (`vector-index-name`).

### 4. Set the environment variables

```bash
export MONGODB_URI="<YOUR_CONNECTION_STRING>" VOYAGE_API_KEY="<API_KEY>"
```

### 5. Run the application

```bash
mvn spring-boot:run
```

### 6. Test the API

Send a `POST` request to the search endpoint:

```
POST http://localhost:8080/movies/search
Content-Type: application/json

{
  "query": "a ship that sinks at night after hitting an iceberg",
  "minIMDbRating": 5,
  "yearFrom": 1980,
  "yearTo": 2003,
  "genres": [
    "Drama", "Action"
  ],
  "excludeGenres": false
}
```

You can also run it directly from `src/main/resources/http/request.http` in your IDE.

#### Or Open the app
```
http://localhost:8080/
```
Use the search bar to find movies (renders title, year, and full plot).

<img src="docs/img/webAppDetails.png" alt="The Movie Search web application"/> 






 
 