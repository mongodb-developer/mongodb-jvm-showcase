# kotlin-driver-search

MongoDB Atlas Search with the Kotlin Sync driver in a Spring Boot REST app — an Airbnb-style property search.

Article: [Discover Your Ideal Airbnb: Implementing a Spring Boot & MongoDB Search with Kotlin Sync Driver](https://foojay.io/today/discover-your-ideal-airbnb-implementing-a-spring-boot-mongodb-search-with-kotlin-sync-driver/)

![Demonstration](./demonstration/demonstration.gif)

## Prerequisites

- Java 21+
- A [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=driver-kotlin-search&utm_term=ricardo.mello) cluster with the `sample_airbnb` dataset and an Atlas Search index on the `summary` field.

## Create the Atlas Search index

In the Atlas UI, go to **Atlas Search → Create Search Index** and create an index with:

- **Database and Collection:** `sample_airbnb` → `listingsAndReviews`
- **Index Name:** `searchPlaces`
- **Definition:**

  ```json
  {
    "mappings": {
      "dynamic": true
    }
  }
  ```

## Run local instead (optional)

Use [mongodb-cli-lab](https://mongodb-cli-lab.vercel.app/) — it spins up a local replica set with Search enabled and imports `sample_airbnb` for you, so you don't need an Atlas cluster.

```bash
npm install -g @ricardohsmello/mongodb-cli-lab

mongodb-cli-lab quickstart --topology replica-set --replicas 3 --search --mongodb-version 8.2 --port 28000 --sample-databases sample_airbnb
```

Then just point the app at it:

```bash
export MONGODB_URI="mongodb://localhost:28000/?directConnection=true"
```

> ⚠️ **You still need to create the `searchPlaces` index** (see the section above). The cli-lab imports the data, but the Atlas Search index is not created automatically — the app won't return results without it.

## Run

**1. Export your connection string:**

```bash
export MONGODB_URI="mongodb+srv://<user>:<password>@<cluster>.mongodb.net/"
```

**2. Run:**

```bash
./gradlew bootRun
```

That's it. The API is up at `http://localhost:8080`.

## Try it

```bash
curl "http://localhost:8080/airbnb/search?query=beach&minNumberReviews=10"
```

- `query` — text to search (full-text, fuzzy) on the `summary` field
- `minNumberReviews` — minimum number of reviews a listing must have
