# Growing Things

A simple RAG app about growing things (plants, gardens, and more).
You ingest some knowledge into the app, then ask questions about it.

The app ingests **5 documents**:

- 3 related to growing plants: a **garden**, **tomatoes**, and **roses**
- 2 about other topics: a **dog** and a **child**

Because answers are grounded in these documents, asking
*"What needs full sun and water?"* returns the plant-related docs
(garden, tomatoes, roses), while *"What needs love and daily care?"*
returns the **dog** and the **child**.

- **Embeddings:** Voyage AI (`voyage-3-large`)
- **Generation:** OpenAI
- **Vector store:** MongoDB Atlas

## How it works

Built with Spring AI.

1. Voyage converts text into embeddings.
2. MongoDB Atlas stores the embeddings and finds the closest ones.
3. OpenAI writes the answer using those documents.
4. MongoDB also keeps the last 5 messages per conversation (that's the `conversationId`).
5. The answer comes back as a `GrowRecommendation` object.

## Requirements

- Java 21+
- A MongoDB Atlas cluster with a vector search index named `spring_ai_index`
  on the `embedding` field, using **1024 dimensions** (Voyage `voyage-3-large`).

## Setup

Export the required environment variables:

```bash
export MONGODB_URI="<your-mongodb-atlas-uri>"
export OPENAI_API_KEY="<your-openai-api-key>"
export VOYAGE_API_KEY="<your-voyage-api-key>"
```

## Run

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

## Usage

1. Ingest the data:

```bash
curl -X POST http://localhost:8080/ingest
```

2. Ask a question:

```bash
curl -X POST http://localhost:8080/chat-rag \
  -H "Content-Type: application/json" \
  -d '{"message": "What needs full sun and water?", "conversationId": "1"}'
```
