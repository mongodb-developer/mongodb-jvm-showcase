# kotlin-ktor-mongodb-vector-search

This is the source code about my <b>MongoDB</b> articles.
- [`Mastering Kotlin: Creating an API With Ktor and MongoDB Atlas`](https://www.mongodb.com/developer/languages/kotlin/mastering-kotlin-creating-api-ktor-mongodb-atlas/)
- [`Beyond Basics: Enhancing Kotlin Ktor API With Vector Search`](https://www.mongodb.com/developer/products/atlas/beyond-basics-enhancing-kotlin-ktor-api-vector-search/)



Kotlin's simplicity, Java interoperability, and Ktor's user-friendly framework combined with MongoDB Atlas' flexible cloud database provide a robust stack for modern software development.
Together, we'll demonstrate and set up the Ktor project, implement CRUD operations, define API route endpoints, and run the application. By the end, you'll have a solid understanding of Kotlin's capabilities in API development and the tools needed to succeed in modern software development.

## MongoDB Atlas Flow
![Generating Embedded Flow](https://i.ibb.co/SQGKK25/first-flow.png)

## Kotlin Ktor Flow
![Application Flow](https://i.ibb.co/JHSFdZc/second-flow.png)

## Built with

- [Kotlin - Programming Language](https://kotlinlang.org/docs/coroutines-overview.html)
- [Ktor - Asynchronous framework](https://ktor.io/)
- [Koin - Dependency Injection framework](https://insert-koin.io/)
- [MongoDB Kotlin Driver — Kotlin Coroutine](https://www.mongodb.com/docs/drivers/kotlin/coroutine/current/)

## Prerequisites

- **JDK 21+**
- A **MongoDB Atlas** cluster ([create a free one](https://cloud.mongodb.com)) — required for Vector Search
- A **Hugging Face token** ([create a free one](https://huggingface.co/settings/tokens)) — used to generate embeddings

## Running

### 1. Set your environment variables

```bash
export MONGO_URI="mongodb+srv://<user>:<pass>@<cluster>/?retryWrites=true&w=majority"
export MONGO_DATABASE="my_database"
export HUGGINGFACE_TOKEN="hf_xxx"
```

### 2. Run the app

```bash
./gradlew run
```

The API starts on **http://localhost:8081**.

### 3. Open Swagger UI

Go to **[http://localhost:8081/swagger-ui](http://localhost:8081/swagger-ui)** to try the endpoints from your browser.

### 4. Seed the exercises

Run `POST /exercises/seed`. This reads the bundled `exercises.json` (~50 exercises), generates an embedding for each, and inserts them into the `exercises` collection.

### 5. Create the Vector Search index

In the Atlas UI (MongoDB Search → Create Index → JSON editor), create an index named **`vector_index`** on the `exercises` collection:

```json
{
  "fields": [
    {
      "type": "vector",
      "path": "descEmbedding",
      "numDimensions": 384,
      "similarity": "cosine"
    }
  ]
}
```

### 6. Search for similar exercises

Run `POST /exercises/processRequest` with a phrase describing what you want. The app converts it into a vector and returns the closest exercises **by meaning**, not by keyword:

```json
{ "input": "exercises to strengthen my core and abs" }
```

## API Endpoints

| Method   | Path                             | Description                                        |
|----------|----------------------------------|----------------------------------------------------|
| `POST`   | `/fitness`                       | Create a fitness record                            |
| `GET`    | `/fitness/{id?}`                 | Get a fitness record by id (or list all)           |
| `GET`    | `/fitness/exerciseType/{type?}`  | Get fitness records by exercise type               |
| `DELETE` | `/fitness/{id?}`                 | Delete a fitness record by id                      |
| `POST`   | `/exercises/seed`                | Seed the `exercises` collection from `exercises.json` (generates embeddings) |
| `POST`   | `/exercises/processRequest`      | Vector Search — find exercises similar to a phrase |

## Swagger UI

To explore the API documentation and try the endpoints from your browser, navigate to:

[http://localhost:8081/swagger-ui](http://localhost:8081/swagger-ui)


