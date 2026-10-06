# quarkus-ai-memory

## Purpose

Quarkus + LangChain4j chat endpoint with conversation memory persisted in MongoDB via `quarkus-langchain4j-memory-store-mongodb`.

## Prerequisites

- Java 21
- MongoDB running locally on `localhost:27017` (or a MongoDB Atlas cluster)
- OpenAI API key

## Configuration

| Variable | Description | Default |
|---|---|---|
| `OPENAI_API_KEY` | OpenAI API key | - |
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017` |

## How to run

```bash
export OPENAI_API_KEY=...
./mvnw quarkus:dev
```

```bash
curl "http://localhost:8080/chat?sessionId=user1&message=My%20name%20is%20Ricardo"
curl "http://localhost:8080/chat?sessionId=user1&message=What%20is%20my%20name"
```

Messages are stored per `sessionId` in MongoDB.

