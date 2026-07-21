# RAG with Java and LangChain4j 

A small **RAG (Retrieval-Augmented Generation)** sample in Java.

Flow:

```
Question
  -> Voyage AI generates the embedding
  -> MongoDB Atlas Vector Search finds the relevant documents
  -> OpenAI answers using that context
  -> Answer
```

## Stack

- Java 21 + Spring Boot 3
- LangChain4j
- MongoDB Atlas (Vector Search)
- Voyage AI (embeddings)
- OpenAI (chat)

## Prerequisites

- JDK 21
- A [MongoDB Atlas](https://www.mongodb.com/atlas?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=github&utm_term=ricardo.mello) cluster
- An [OpenAI](https://platform.openai.com) API key
- A [Voyage AI](https://www.voyageai.com) API key
  
## 1. Set the environment variables

```bash
export OPENAI_API_KEY=sk-...
export VOYAGE_API_KEY=pa-...
export MONGODB_URI="mongodb+srv://user:password@cluster.mongodb.net"
```

## 2. Run the application

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

## 3. Try it

Use the `requests.http` file (click ▶ in IntelliJ) or `curl`. **Run the steps in order** — `/chat` and `/search` only work after the documents are ingested.

```bash
# 1. Load the documents into MongoDB (run ONCE)
#    Documents -> Voyage AI -> MongoDB Atlas
curl -X POST localhost:8080/ingest

# 2. Semantic search only (no LLM) — see what Vector Search returns
curl -X POST localhost:8080/search \
  -H 'Content-Type: application/json' \
  -d '{"question":"What types of insects live in the rainforest?"}'

# 3. Full RAG — same search, but OpenAI writes the answer
curl -X POST localhost:8080/chat \
  -H 'Content-Type: application/json' \
  -d '{"question":"What types of insects live in the rainforest?"}'

# 4. Health check
curl localhost:8080/health
```

## Why `/search` exists

`/chat` runs the whole RAG pipeline, but the retrieval step is hidden inside
LangChain4j's `AiServices`, so you can't actually see the semantic search happen.

`/search` exposes that hidden step on its own: it embeds the question with Voyage,
runs MongoDB Atlas Vector Search, and returns the matching documents with their
similarity **score** — no OpenAI involved. It is the "retrieval" half of RAG made
visible.

Comparing the two makes the concept click:

- `/search` → the raw documents the database retrieved.
- `/chat`   → the same documents, turned into a natural-language answer by OpenAI.

In short: `/chat` ≈ `/search` + sending those documents to the LLM as context.

## Notes

- Run `/ingest` **only once** — calling it again duplicates the documents.
  To reset, drop the `rainforest_docs` collection in Atlas.
- After the first `/ingest`, wait a few seconds: Atlas needs a moment to make the
  Vector Search index queryable.
